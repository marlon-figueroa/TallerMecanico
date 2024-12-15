package com.TP.TallerMecanico.interfaz;
import com.TP.TallerMecanico.entidad.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface IClienteDao extends CrudRepository<Cliente, Long> {

    boolean existsByIdClienteAndEstadoTrue(Long id);

    @Modifying
    @Query("UPDATE Cliente m SET m.estado = false WHERE m.idCliente = :idCliente") //Query para el Soft Delete
    void marcarComoEliminado(@Param("idCliente") Long idCliente);

    List<Cliente> findByEstadoTrue();
    List<Cliente> findByEstadoFalse();

    @Modifying
    @Query("UPDATE Cliente m SET m.estado = true WHERE m.idCliente = :idCliente")
    void marcarComoActivo(@Param("idCliente") Long idCliente);

    Cliente findByDui(String dui);

    Cliente findByIdCliente(Long idCliente);

    Cliente findByDuiAndEstadoTrue(String dui);


    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE :nombre% AND c.estado = true")
    List<Cliente> findByNombreStartingWithAndEstadoTrue(@Param("nombre") String nombre);


    @Query("SELECT c FROM Cliente c WHERE c.dui LIKE :dui% AND c.estado = true")
    List<Cliente> findByDuiStartingWithAndEstadoTrue(@Param("dui") String dui);

    @Query("SELECT c FROM Cliente c WHERE c.dui LIKE :dui% AND c.estado = false")
    List<Cliente> findByDuiStartingWithAndEstadoFalse(@Param("dui") String dui);


    //List<Cliente> findByNombre(String nombre);

}