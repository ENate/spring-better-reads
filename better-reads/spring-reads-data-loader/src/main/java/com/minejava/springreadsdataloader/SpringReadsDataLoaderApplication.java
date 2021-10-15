package com.minejava.springreadsdataloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.minejava.springreadsdataloader.author.Author;
import com.minejava.springreadsdataloader.author.AuthorRepository;
import com.minejava.springreadsdataloader.book.Book;
import com.minejava.springreadsdataloader.book.BookRepository;
import com.minejava.springreadsdataloader.connection.DataStaxAstraProperties;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class SpringReadsDataLoaderApplication {

	@Autowired
	AuthorRepository authorRepository;

	@Autowired
	BookRepository bookRepository;

	@Value("${datadump.location.author}")
	private String authorDumpLocation;

	@Value("${datadump.location.works}")
	private String worksDumpLocation;

	public static void main(String[] args) {

		SpringApplication.run(SpringReadsDataLoaderApplication.class, args);
	}

	// initialize values of authors
	private void initAuthors() {
		Path path = Paths.get(authorDumpLocation);
		// Line by line reads
		try (Stream<String> lines = Files.lines(path)) {
			// Treat individual lines as streams
			// Read and parse line: get position of the first curly brace
			lines.forEach(line -> {
				String jsonString = line.substring(line.indexOf("{"));
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
					// Construct Author Object
					Author author = new Author();
					author.setName(jsonObject.optString("name"));
					author.setPersonalName(jsonObject.optString("personal_name"));
					author.setId(jsonObject.optString("key").replace("/authors/", ""));
					// Persist in the repository
					System.out.println(" Saving author " + author.getName() + "...");
					authorRepository.save(author);
				} catch (JSONException e) {
					System.out.println("Not working");
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			// print stack trace
			e.printStackTrace();
		}
	};

	// initialize values of authors
	private void initWorks() {
		// Implement the works
		Path pathWorks = Paths.get(worksDumpLocation);
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		try (Stream<String> lines = Files.lines(pathWorks)) {
			lines.limit(20).forEach(line -> {
				String jsonString = line.substring(line.indexOf("{"));
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
					// Construct Book Object
					// Get book info and put it to new book instance like in Author case...
					Book book = new Book();
					book.setId(jsonObject.getString("key").replace("/works/", "replacement"));
					book.setName(jsonObject.optString("title"));
					// Get description dot value
					JSONObject descriptionObj = jsonObject.optJSONObject("description");
					if (descriptionObj != null) {
						book.setDescription(descriptionObj.optString("value"));
					}
					// added published data by creating object
					JSONObject publishedObj = jsonObject.optJSONObject("created");
					if (publishedObj != null) {
						String dateStr = publishedObj.getString("value");
						book.setPublishedDate(LocalDate.parse(dateStr, dateTimeFormat));
					}
					JSONArray coversJSONArr = jsonObject.optJSONArray("covers");
					if (coversJSONArr != null) {
						List<String> coverIds = new ArrayList<>();
						for (int i = 0; i < coversJSONArr.length(); i++) {
							coverIds.add(coversJSONArr.getString(i));
						}
						book.setCoverIds(coverIds);
					}

					JSONArray authorsJSONArr = jsonObject.optJSONArray("authors");
					if (authorsJSONArr != null) {
						List<String> authorIds = new ArrayList<>();
						for (int i = 0; i < authorsJSONArr.length(); i++) {
							String authorId = authorsJSONArr.getJSONObject(i).getJSONObject("author").getString("key")
									.replace("/authors/", "");
							authorIds.add(authorId);
						}
						book.setAuthorIds(authorIds);

						List<String> authorNames = authorIds.stream().map(id -> authorRepository.findById(id))
								.map(optionalAuthor -> {
									if (!optionalAuthor.isPresent())
										return "Unknown Author";
									return optionalAuthor.get().getName();
								}).collect(Collectors.toList());
						book.setAuthorNames(authorNames);
						// Persist the book repo
						System.out.println("Saving the book " + book.getName() + "...");
						bookRepository.save(book);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

		} catch (IOException e) {
			//
		}

	}

	@PostConstruct
	public void start() {
		/**
		 * Author author = new Author(); author.setId("NHT67g");
		 * author.setName("Chinua"); author.setPersonalName("Chinua Achebe");
		 * authorRepository.save(author);
		 * 
		 * Author author2 = new Author(); author2.setId("HL34TRR");
		 * author2.setName("Harper"); author2.setPersonalName("Harper Lee");
		 * authorRepository.save(author2);
		 * 
		 * Author author3 = new Author(); author3.setId("KG34TRR");
		 * author3.setName("Harper"); author3.setPersonalName("Josh MccMillian");
		 * authorRepository.save(author3);
		 */
		// initAuthors();
		initWorks();
		System.out.println(authorDumpLocation);
	}

	/**
	 * This is necessary to have the Spring Boot app use the Astra secure bundle to
	 * connect to the database
	 */
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

}
