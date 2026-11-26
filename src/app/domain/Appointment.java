package app.domain;

import java.time.LocalDateTime;
import java.util.UUID ;
import java.util.Objects;


public enum AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELED
}

public class Appointment {

    private final UUID id;
    private final UUID patientId ;
    private final UUID doctorId ;
    private LocalDateTime dateTime ;
    private AppointmentStatus status ;
    
    
    public Appointment (UUID patientId ,UUID doctorId ,LocalDateTime dateTime) {
        
        this.id = UUID.randomUUID();
        
        this.patientId = Objects.requireNonNull(patientId);
        this.doctorId = Objects.requireNonNull(doctorId);
        
        if (dateTime == null || !dateTime.isAfter(LocalDateTime.now())){
            throw new IllegalArgumentException ("Appointment date must be in the future");
        }
        this.dateTime = dateTime;
        
        this.status = AppointmentStatus.SCHEDULED;
        
    }
    
    
    public Appointment(UUID id, UUID patientId, UUID doctorId, LocalDateTime dateTime, AppointmentStatus status){
        
        this.id = Objects.requireNonNull(id);
        this.patientId = Objects.requireNonNull(patientId);
        this.doctorId = Objects.requireNonNull(doctorId);
        this.dateTime = Objects.requireNonNull(dateTime);
        this.status = Objects.requireNonNull(status);
    }
    
    public void reschedule (LocalDateTime newDateTime){
        
        if (status != AppointmentStatus.SCHEDULED){
            throw new IllegalArgumentException ("Only scheduled appointments can be rescheduled");
        }
        
        
        if (newDateTime == null || !newDateTime.isAfter(LocalDateTime.now())){
            throw new IllegalArgumentException ("New Appointment date must be in the future");
        }
        this.dateTime = newDateTime;
        
        
    }
    
    
    public void complete() {
        
        if (status != AppointmentStatus.SCHEDULED){
            throw new IllegalArgumentException ("Only scheduled appointments can be completed");
        }
        this.status = AppointmentStatus.COMPLETED;
    }
    
    
    public void cancel() {
        
        if (status != AppointmentStatus.SCHEDULED){
            throw new IllegalArgumentException ("Only scheduled appointments can be canceled");
        }
        this.status = AppointmentStatus.CANCELED;
    }
    
    
    public UUID getId() {
        return id ;
    }
    
    public UUID getPatientId() {
        return patientId ;
    }
    
    public UUID getDoctorId() {
        return doctorId ;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime ;
    }
    
    public AppointmentStatus getStatus() {
        return status ;
    }
}


