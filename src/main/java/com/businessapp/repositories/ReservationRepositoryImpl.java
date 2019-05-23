package com.businessapp.repositories;

import com.businessapp.model.Reservation;

import java.util.Date;
import java.util.List;

public class ReservationRepositoryImpl extends GenericMemRepositoryImpl<Reservation> implements ReservationRepositoryIntf {


    ReservationRepositoryImpl(List<Reservation> list) {
        super(list);
    }

    @Override
    public Reservation create() {
        return new Reservation( new Date() );
    }
}
