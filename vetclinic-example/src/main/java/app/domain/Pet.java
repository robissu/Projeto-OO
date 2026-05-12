package app.domain;

/** Animal cadastrado na clínica. */
public class Pet {
    private int    id;
    private String name;
    private String species;   // cão, gato, pássaro, etc.
    private String breed;
    private int    age;
    private int    ownerId;

    public Pet() {}

    public Pet(int id, String name, String species, String breed, int age, int ownerId) {
        this.id      = id;
        this.name    = name;
        this.species = species;
        this.breed   = breed;
        this.age     = age;
        this.ownerId = ownerId;
    }

    public int    getId()      { return id;      }
    public String getName()    { return name;    }
    public String getSpecies() { return species; }
    public String getBreed()   { return breed;   }
    public int    getAge()     { return age;     }
    public int    getOwnerId() { return ownerId; }

    public void setId(int id)            { this.id      = id;      }
    public void setName(String name)     { this.name    = name;    }
    public void setSpecies(String sp)    { this.species = sp;      }
    public void setBreed(String breed)   { this.breed   = breed;   }
    public void setAge(int age)          { this.age     = age;     }
    public void setOwnerId(int ownerId)  { this.ownerId = ownerId; }

    @Override public String toString() {
        return "[%d] %s | %s | %s | %d ano(s) | Tutor ID: %d"
                .formatted(id, name, species, breed, age, ownerId);
    }
}
