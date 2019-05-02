package gradle.cucumber;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.text.RandomStringGenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InsertDataStepDefs {
    private Connection connection;

    private String createUserBy(int id, String name) {
        return String.format("INSERT INTO `users` (user_id, name) VALUES (%d, '%s');", id, name);
    }

    private String createTweetBy(int user_id, String content) {
        return String.format("INSERT INTO `tweets` (user_id, content) " +
                "VALUES " +
                "(%d, '%s');", user_id, content);
    }

    private static String generateRandomString(int length) {
        return new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .build()
                .generate(length);
    }

    @Given("^DBの設定をする$")
    public void setupDB() throws Throwable {
        String url = "jdbc:mysql://"
                + Optional.ofNullable(System.getenv("DB_HOST")).orElse("localhost")
                + ":"
                + Optional.ofNullable(System.getenv("DB_PORT")).orElse("3306")
                + "/dummydb";
        connection = DriverManager.getConnection(url,
                Optional.ofNullable(System.getenv("DB_USER")).orElse("root"),
                Optional.ofNullable(System.getenv("DB_PASSWORD")).orElse("root")
        );
    }

    @When("^id (\\d+) name \"([^\"]*)\" のユーザを作る$")
    public void createUser(int id, String name) throws Throwable {
        try {
            Statement statement = connection.createStatement();
            statement.execute(this.createUserBy(id, name));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @When("^つぶやきを作る$")
    public void createTweet(DataTable table) throws Throwable {
        Stream<Map<String, String>> tweets = table.asMaps().stream();
        tweets.map(tweet -> this.createTweetBy(
                Integer.valueOf(tweet.get("user_id")), tweet.get("content"))
        ).forEach(s -> {
            try {
                Statement statement = connection.createStatement();
                statement.execute(s);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

    @When("^user_id (\\d+) のユーザに ランダムな内容(\\d+)個のつぶやきを作る$")
    public void createRandomTweets(int userId, int numOfTweet) throws Throwable {
        IntStream.range(0, numOfTweet).boxed()
                .map(i -> this.createTweetBy(userId, generateRandomString(200)))
                .forEach(s -> {
                    try {
                        Statement statement = connection.createStatement();
                        statement.execute(s);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Then("^コネクションを閉じる$")
    public void connectionClose() throws Throwable {
        connection.close();
    }
}
