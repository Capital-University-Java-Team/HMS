package app.domain;


import java.time.LocalDateTime;
import java.util.UUID ;


public class Appointment {

    private final String id;
    private final String patientId ;
    private final String doctorId ;
    private LocalDateTime dateTime ;
    private AppointmentStatus status ;
    
    
    public Appointment (String patientId ,String doctorId ,LocalDateTime dateTime) {
        
        this.id = UUID.randomUUID().toString().substring(0, 8);
        
        this.patientId = DomainException.requireNonBlank(patientId, "Patient ID is required.");
        this.doctorId = DomainException.requireNonBlank(doctorId, "Doctor ID is required.");
        
        validateFutureDate(dateTime);
        this.dateTime = dateTime;
        
        this.status = AppointmentStatus.SCHEDULED;
        
    }
    
    
    private void validateFutureDate(LocalDateTime dateTime) {

        DomainException.requireNonNull(dateTime, "Appointment date is required.");

        if (!dateTime.isAfter(LocalDateTime.now())) {
            throw new DomainException("Appointment date must be in the future.");
        }
    }
    
    
    public Appointment(String id, String patientId, String doctorId, LocalDateTime dateTime, AppointmentStatus status){
        
        this.id = DomainException.requireNonBlank(id, "Appointment ID is required.");
        this.patientId = DomainException.requireNonBlank(patientId, "Patient ID is required.");
        this.doctorId = DomainException.requireNonBlank(doctorId, "Doctor ID is required.");
        this.dateTime = DomainException.requireNonNull(dateTime, "Appointment date is required.");
        this.status = DomainException.requireNonNull(status, "Appointment status is required.");
    }
    
    public void reschedule (LocalDateTime newDateTime){
        
        if (status != AppointmentStatus.SCHEDULED){
            throw new DomainException("Only scheduled appointments can be rescheduled");
        }
        
        validateFutureDate(newDateTime);
        this.dateTime = newDateTime;
        
        
    }
    
    
    public void complete() {
        
        if (status != AppointmentStatus.SCHEDULED){
            throw new DomainException("Only scheduled appointments can be completed");
        }
        this.status = AppointmentStatus.COMPLETED;
    }
    
    
    public void cancel() {
        
        if (status != AppointmentStatus.SCHEDULED){
            throw new DomainException("Only scheduled appointments can be canceled");
        }
        this.status = AppointmentStatus.CANCELED;
    }
    
    
    public String getId() {
        return id ;
    }
    
    public String getPatientId() {
        return patientId ;
    }
    
    public String getDoctorId() {
        return doctorId ;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime ;
    }
    
    public AppointmentStatus getStatus() {
        return status ;
    }

}

