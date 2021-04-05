package live.socialchat.auth.user;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import live.socialchat.auth.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

@Repository
public class MongoUserRepository implements UserRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoUserRepository.class);
    private static final String USER_COLLECTION_NAME = "user";
    private static final String USER_ID = "_id";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    
    private final MongoCollection<User> mongoCollection;
    
    @Autowired
    public MongoUserRepository(final MongoDatabase mongoDatabase) {
        this.mongoCollection = mongoDatabase.getCollection(USER_COLLECTION_NAME, User.class);
    }
    
    @Override
    public Mono<User> create(final User user) {
        return Mono.from(mongoCollection.insertOne(user))
            .doOnSuccess(result -> LOGGER.info("Inserted user {}", result.getInsertedId()))
            .doOnError(error -> LOGGER.info("Failed to insert user. Reason: {}", error.getMessage()))
            .flatMap(result -> Mono.just(user));
    }

    @Override
    public Mono<User> findFullDetailsByUsername(final String username) {
        return Mono.from(
                mongoCollection
                    .find(eq(USERNAME, username))
                    .first()
            );
    }

    @Override
    public Flux<User> findByUsernameOrEmail(final String email, final String username) {
        return Flux.from(
                mongoCollection.find(
                    or(
    
                        Filters.text()
                        
                        eq(EMAIL, email),
                        eq(USERNAME, username)
                    )
                )
                .projection(
                    fields(
                        include(USER_ID, EMAIL, USERNAME)
                    )
                )
                .limit(2)
            );
    }

}
