package db.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.TableGenerator;

/**
 * Entity implementation class for Entity: SomeEntity Remember to run the sequenceTable.sql to create the database table
 * otherwise deployment errors will happen.
 */
@Entity
public class SomeEntity implements Serializable {

    @Id
    @TableGenerator(name = "idseq", allocationSize = 1, table = "testcaseSeq", pkColumnName = "seq_name", valueColumnName = "seq_count")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "idseq")
    private int id;

    @javax.persistence.Version
    @javax.persistence.Column(name = "TRANSACTION_COUNT")
    private Integer transactionCount;

    @Lob
    @Column(name = "TEXTLOB")
    private String text;

    private static final long serialVersionUID = 1L;

    // //////////////////////////////
    // BEGIN: BOILER PALTE
    // //////////////////////////////
    public SomeEntity() {
        super();
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
