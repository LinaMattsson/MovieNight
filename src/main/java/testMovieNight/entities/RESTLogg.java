package testMovieNight.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RESTLogg {
    @Id
    private Integer id;
    private String successOrError;
    private String IPAddress;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSuccessOrError() {
        return successOrError;
    }

    public void setSuccessOrError(String successOrError) {
        this.successOrError = successOrError;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }
}
