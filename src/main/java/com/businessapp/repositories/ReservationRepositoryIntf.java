package com.businessapp.repositories;

import com.businessapp.model.Reservation;

import java.util.List;

public interface ReservationRepositoryIntf extends RepositoryIntf<Reservation> {

    public Reservation create();

    public List<Reservation> findAll();

    public Reservation findById( String id );

    public Reservation update( Reservation r, boolean insert );

    public void delete( String id );

    public void delete( List<String> ids );

    public void deleteAll();

}
