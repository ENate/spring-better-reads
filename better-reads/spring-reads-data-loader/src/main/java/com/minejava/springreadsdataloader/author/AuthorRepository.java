package com.minejava.springreadsdataloader.author;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

// For persisting and fetching data from cassandra
@Repository
public interface AuthorRepository extends CassandraRepository<Author, String> {

}
