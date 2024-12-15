package com.TP.TallerMecanico.servicio;

import com.TP.TallerMecanico.entidad.Cliente;
import com.TP.TallerMecanico.entidad.Vehiculo;
import com.TP.TallerMecanico.interfaz.IClienteDao;
import com.TP.TallerMecanico.interfaz.IVehiculoDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteImplementacion implements IClienteService {

    //A continuacion se instancian interfaces, gracias al @Autowired de Spring, podemos instanciar clases abstractas e inyectarle
    //los metodos de una clase que implemente esta interfaz
    
    @Autowired
    private IVehiculoDao vehiculoDao;

    @Autowired
    private IVehiculoService vehiculoService;

    @Autowired
    private IClienteDao clienteDao;

    //A continuacion todos los metodos de la clase

    @Override
    public List<Cliente> buscarClienteNombre(String nombre){
        return clienteDao.findByNombreStartingWithAndEstadoTrue(nombre);
    }

    @Override
    @Transactional
    public List<Cliente> buscarClienteDui(String dui){
        return clienteDao.findByDuiStartingWithAndEstadoTrue(dui);
    }

    @Override
    @Transactional(readOnly = true)
    //Metodo para listar todos los clientes activos
    public List<Cliente> listarClientes() { 
        return clienteDao.findByEstadoTrue();  //Devuelve una lista de clientes en estadoTrue
    }

    @Override
    @Transactional //Anotacion para controlar que las operaciones se ejecuten de manera correcta 
    public void guardar(Cliente cliente) {//Metodo para guardar un nuevo cliente
        
        //Setteamos los valores ingresados de nombre y apellido en mayusculas
        cliente.setNombre(cliente.getNombre().toUpperCase()); 
        cliente.setApellido(cliente.getApellido().toUpperCase()); 
        
        //Creamos 3 variables de entorno para guardar el dui del cliente, y para buscar y guardar clientes
        //en base al dui y al estado
        String dui = cliente.getDui();

        //Metodos de clienteDao, que se encargan de comunicarse con la base de datos para obtener (o no) un objeto cliente especifico
        Cliente duiExistente = clienteDao.findByDui(dui);
        Cliente duiActivado = clienteDao.findByDuiAndEstadoTrue(dui);
        
        //Aca empieza la logica del guardado
        
        //Si el dui ingresado no existe en la BD se guarda el cliente 
        if (duiExistente == null) {
            clienteDao.save(cliente);
        
        //Caso contrario    
        } else {

            //Verificamos si el cliente con el mismo dui se encuentra activado en la base de datos 
            if (duiActivado == null){
                
                //Llamamos al metodo activarCliente 
                activarCliente(duiExistente);

                //Metodo para sobreescribir un cliente eliminado con datos diferentes a los cargados (Ver)
                if (!duiExistente.equals(cliente)){
                    cliente.setIdCliente(duiExistente.getIdCliente());
                    clienteDao.save(cliente);
                }
            }
        }
    }


    @Override
    @Transactional
    public void actualizar(Cliente cliente){//Metodo para actualizar un cliente existente activado 
        
        //Setteamos los valores ingresados de nombre y apellido en mayusculas
        cliente.setNombre(cliente.getNombre().toUpperCase());
        cliente.setApellido(cliente.getApellido().toUpperCase());

        //Creamos 3 variables de entorno, en una guardamos el Id del cliente, y en las otras hacemos
        //busquedas por id y por dui, respectivamente
        Long clienteId = cliente.getIdCliente();

        //Metodos de clienteDao, que se encargan de comunicarse con la base de datos para obtener (o no) un objeto cliente especifico
        Cliente clienteExistente = clienteDao.findById(clienteId).orElse(null);
        Cliente clienteByDui = clienteDao.findByDui(cliente.getDui());

        //En el caso de que el dui ingresado ya exista en la base de datos y se encuentre en estado eliminado(false)
        //lo activamos
        if (clienteByDui != null){
            activarCliente(clienteByDui);
        }

        //Aca empieza la logica del actualizar

        //Si ya existe un cliente en la BD con ese id (solo en casos especiales no se cumplirira)
        if (clienteExistente != null) {
            
            //Guardamos los nuevos datos cargados por el usuario
            String nuevoDui = cliente.getDui();
            String duiExistente = clienteExistente.getDui();

            //Controlamos que el dui nuevo sea igual al existente, o que el dui nuevo no exista en la base de datos
            if (nuevoDui.equals(duiExistente) || !duiExisteEnBaseDeDatos(nuevoDui)) {
                //Guardamos el cliente
                clienteDao.save(cliente);
            }
        }
    }

    //Metodo boolean que nos sirve para simplificar el chequeo realizado en la linea 109
    private boolean duiExisteEnBaseDeDatos(String duiCliente) {
        
        //Devuelve true si encuentra un objeto
        return clienteDao.findByDui(duiCliente) != null;
    }

    @Override
    @Transactional
    public void eliminar(Cliente cliente) { //Metodo para eliminar un cliente (borrado logico), con la logica para que sea en cascada
        
        //Llamamos al metodo marcarComoEliminado del clienteDao para cambiar el estado del cliente a false
        clienteDao.marcarComoEliminado(cliente.getIdCliente());
        
        //Realizamos un ciclo que recorra todos los vehiculos asociados al id de ese cliente y llamamos al metodo
        //eliminar de cada vehiculo, el cual ejecutara el mismo metodo marcarComoEliminado de vehiculoDao
        for (Vehiculo vehiculo : vehiculoDao.findByClienteAndEstadoTrue(cliente)){
            vehiculoService.eliminar(vehiculo);
        }
    }

    @Override
    @Transactional(readOnly = true)
    //Metodo para buscar un cliente en base a su id
    public Cliente buscarCliente(Cliente cliente) {
        return clienteDao.findById(cliente.getIdCliente()).orElse(null);
    }

    @Override
    @Transactional
    //Metodo para activar un cliente
    public void activarCliente(Cliente cliente){
        
        //Llamamos al metodo marcarComoActivo del clienteDao para cambiar el estado del cliente a true
        clienteDao.marcarComoActivo(cliente.getIdCliente());

        //Realizamos un ciclo que recorra todos los vehiculos asociados al id de ese cliente y llamamos al metodo
        //al metodo activar de cada vehiculo, el cual ejecutara el mismo metodo marcarComoActivo de vehiculoDao
        for (Vehiculo vehiculo : cliente.getVehiculos()){
            vehiculoService.activarVehiculo(vehiculo);
        }
    }
}