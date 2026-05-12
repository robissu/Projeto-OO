package app.domain;

/** Tutor / Proprietário do animal. */
public class Owner {
    private int    id;
    private String name;
    private String phone;
    private String email;

    public Owner() {}

    public Owner(int id, String name, String phone, String email) {
        this.id    = id;
        this.name  = name;
        this.phone = phone;
        this.email = email;
    }

    public int    getId()    { return id;    }
    public String getName()  { return name;  }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setId(int id)         { this.id    = id;    }
    public void setName(String name)  { this.name  = name;  }
    public void setPhone(String ph)   { this.phone = ph;    }
    public void setEmail(String em)   { this.email = em;    }

    @Override public String toString() {
        return "[%d] %s | %s | %s".formatted(id, name, phone, email);
    }
}
