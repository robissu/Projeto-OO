package app.domain;

/** Consulta agendada na clínica. */
public class Appointment {
    private int    id;
    private int    petId;
    private int    vetId;
    private String date;     // "AAAA-MM-DD"
    private String time;     // "HH:MM"
    private String reason;
    private String notes;

    public Appointment() {}

    public Appointment(int id, int petId, int vetId,
                       String date, String time, String reason, String notes) {
        this.id     = id;
        this.petId  = petId;
        this.vetId  = vetId;
        this.date   = date;
        this.time   = time;
        this.reason = reason;
        this.notes  = notes;
    }

    public int    getId()     { return id;     }
    public int    getPetId()  { return petId;  }
    public int    getVetId()  { return vetId;  }
    public String getDate()   { return date;   }
    public String getTime()   { return time;   }
    public String getReason() { return reason; }
    public String getNotes()  { return notes;  }

    public void setId(int id)           { this.id     = id;     }
    public void setPetId(int petId)     { this.petId  = petId;  }
    public void setVetId(int vetId)     { this.vetId  = vetId;  }
    public void setDate(String date)    { this.date   = date;   }
    public void setTime(String time)    { this.time   = time;   }
    public void setReason(String r)     { this.reason = r;      }
    public void setNotes(String n)      { this.notes  = n;      }

    @Override public String toString() {
        return "[%d] %s %s | Pet ID: %d | Vet ID: %d | %s"
                .formatted(id, date, time, petId, vetId, reason);
    }
}
