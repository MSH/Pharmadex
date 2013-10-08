package org.msh.pharmadex.service;

import org.msh.pharmadex.dao.iface.AppointmentDAO;
import org.msh.pharmadex.domain.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Author: usrivastava
 */
@Service
public class AppointmentService implements Serializable{

    @Autowired
    AppointmentDAO appointmentDAO;

    private List<Appointment> appointments;

    public List<Appointment> getAppointments(){
        return (List<Appointment>) appointmentDAO.findAll();
    }


}
