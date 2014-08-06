package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Author: usrivastava
 */
@Entity
@Table(name = "resource_message")
@NamedQueries({
        @NamedQuery(name = ResourceMessage.QUERYNAME_FIND_ALL,
                query = ResourceMessage.QUERY_FIND_ALL),
        @NamedQuery(name = ResourceMessage.QUERYNAME_COUNT_ALL,
                query = ResourceMessage.QUERY_COUNT_ALL)})
public class ResourceMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    public static final String QUERYNAME_FIND_ALL = "ResourceMessage.findAll";
    public static final String QUERY_FIND_ALL = "Select r from ResourceMessage r";

    public static final String QUERYNAME_COUNT_ALL = "ResourceMessage.countAll";
    public static final String QUERY_COUNT_ALL = "select count(r) from ResourceMessage r";

    @Column(name = "message_key")
    private String key;

    @Column(name = "message_value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "key_bundle")
    private ResourceBundle bundle;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
