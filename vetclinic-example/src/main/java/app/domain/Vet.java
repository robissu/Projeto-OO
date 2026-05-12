package app.domain;

/** Médico veterinário. */
public class Vet {
    private int    id;
    private String name;
    private String specialty;
    private String crmv;       // Registro profissional

    public Vet() {}

    public Vet(int id, String name, String specialty, String crmv) {
        this.id        = id;
        this.name      = name;
        this.specialty = specialty;
        this.crmv      = crmv;
    }

    public int    getId()        { return id;        }
    public String getName()      { return name;      }
    public String getSpecialty() { return specialty; }
    public String getCrmv()      { return crmv;      }

    public void setId(int id)              { this.id        = id;        }
    public void setName(String name)       { this.name      = name;      }
    public void setSpecialty(String sp)    { this.specialty = sp;        }
    public void setCrmv(String crmv)       { this.crmv      = crmv;      }

    @Override public String toString() {
        return "[%d] Dr(a). %s | %s | CRMV: %s".formatted(id, name, specialty, crmv);
    }
}
