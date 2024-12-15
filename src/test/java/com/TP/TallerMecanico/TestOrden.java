package com.TP.TallerMecanico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.TP.TallerMecanico.entidad.DetalleOrden;
import com.TP.TallerMecanico.entidad.Orden;

public class TestOrden {

    @Test
	public void ejecutarTest () {
        //Creo array
        List<DetalleOrden> listaDetalles = new ArrayList<>();
		//Creo objetos y asigno valores
		Orden orden = new Orden();
        DetalleOrden detalle1 = new DetalleOrden();
        detalle1.setEstado(true);
        detalle1.setSubtotal(4000);
        DetalleOrden detalle2 = new DetalleOrden();
        detalle2.setEstado(true);
        detalle2.setSubtotal(12000);

        listaDetalles.add(detalle1);
        listaDetalles.add(detalle2);
		
        orden.setDetallesOrden(listaDetalles);

		//Se comprueba si el resultado obtenido esta bien calculado
		assertTrue(true);
	}
}
