package db_praktikum.entities_collection;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contributor")
public class Contributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contributor_id", nullable = false)
    private Integer contributorId;

    @Column(name = "contributor_name", length=200, nullable = false)
    private String contributorName;

    public Contributor() {
    }

    public Contributor(String contributorName) {
        this.contributorName = contributorName;
    }

    public Integer getContributorId() {
        return contributorId;
    }

    public void setContributorId(Integer contributorId) {
        this.contributorId = contributorId;
    }

    public String getContributorName() {
        return contributorName;
    }

    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
    }
    @Override
    public String toString(){
        return "Contributor{" +
                "contributorId=" + contributorId +
                ", contributorName='" + contributorName + '\'' +
                '}';
    }

      

}
