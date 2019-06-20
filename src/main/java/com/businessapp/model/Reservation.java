package com.businessapp.model;

import com.businessapp.logic.IDGenerator;
import com.businessapp.model.customserializer.ReservationJSONDeserializer;
import com.businessapp.model.customserializer.ReservationJSONSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonSerialize(using = ReservationJSONSerializer.class)
@JsonDeserialize(using = ReservationJSONDeserializer.class)

public class Reservation implements EntityIntf {
    private static final long serialVersionUID = 1L;

    private final String id;	// Unique, non-null Reservation id.

    private String cid;	// corresponding CustomerID

    private Date date;

    private final List<String> aids = new ArrayList<>();	// List of corresponding ArticleIDs


    // Customer status.
    public enum ReservationStatus { ACTIVE, SUSPENDED, TERMINATED, NOT_CONFIRMED };
    //
    private ReservationStatus status;

    public Reservation(){
        this(new Date());
    }

    public Reservation(Date date){
        this(null, null, date);
    }

    private static final IDGenerator IDG = new IDGenerator( "R.", IDGenerator.IDTYPE.AIRLINE, 6 );

    public Reservation(String id, String cid, Date date) {
        this.id = id==null ? IDG.nextId() : id;
        this.cid = cid==null ? "C." : cid;
        this.status = ReservationStatus.ACTIVE;
        this.date = date;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getCustomerId(){
        return cid;
    }

    public void setCustomerId(String cid){
        this.cid = cid;
    }


    public List<String> getArticleIds(){
        return this.aids;
    }

    public void setArticle( String aid ){
        this.aids.add(aid);
    }


    public ReservationStatus getStatus (){
        return status;
    }

    public void setStatus(ReservationStatus status){
        this.status = status;
    }


    public Date getDate() {
        return date;
    }

    public Long getDateAsTimestamp() {
        return date.getTime();
    }

    public void setDate(Date date){
        this.date = date;
    }

}
