package com.minejava.springreadsdataloader.book;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

@Table(value = "book_by_id")
public class Book {
    // primary key column allows specifications for name etc
    @Id
    @PrimaryKeyColumn(name = "book_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String id; // pk or partition key

    @Column("book_name")
    @CassandraType(type = Name.TEXT)
    private String name;

    // Book description
    @Column("book_description")
    @CassandraType(type = Name.TEXT)
    private String description;

    // published date as localDate object because of good format
    @Column("published_date")
    @CassandraType(type = Name.DATE)
    private LocalDate publishedDate;

    @Column("cover_ids") // type of list with string elements
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> coverIds;

    // denormalize using author names
    @Column("author_names") // type of list with string elements
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> authorNames;

    @Column("author_id") // type of list with string elements
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> authorIds;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the publishedDate
     */
    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    /**
     * @param publishedDate the publishedDate to set
     */
    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    /**
     * @return the coverIds
     */
    public List<String> getCoverIds() {
        return coverIds;
    }

    /**
     * @param coverIds the coverIds to set
     */
    public void setCoverIds(List<String> coverIds) {
        this.coverIds = coverIds;
    }

    /**
     * @return the authorNames
     */
    public List<String> getAuthorNames() {
        return authorNames;
    }

    /**
     * @param authorNames the authorNames to set
     */
    public void setAuthorNames(List<String> authorNames) {
        this.authorNames = authorNames;
    }

    /**
     * @return the authorIds
     */
    public List<String> getAuthorIds() {
        return authorIds;
    }

    /**
     * @param authorIds the authorIds to set
     */
    public void setAuthorIds(List<String> authorIds) {
        this.authorIds = authorIds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */

    @Override
    public String toString() {
        return "Book [authorIds=" + authorIds + ", authorNames=" + authorNames + ", coverIds=" + coverIds
                + ", description=" + description + ", id=" + id + ", name=" + name + ", publishedDate=" + publishedDate
                + "]";
    }

}
