# scribe-java-bitbucket

These classes makes it possible to use Bitbucket API from [Scribe](https://github.com/fernandezpablo85/scribe-java/), like this.

```java
OAuthService service = new ServiceBuilder()
                                  .provider(BitbucketApi20.class)
                                  .apiKey(BITBUCKET_API_KEY)
                                  .apiSecret(BITBUCKET_API_SECRET)
                                  .build();
```

See the documentation for [Scribe](https://github.com/fernandezpablo85/scribe-java/) for more usage instructions.

The project is available from Maven Central:

```xml
<dependency>
    <groupId>se.mnord.scribe</groupId>
    <artifactId>scribe-java-bitbucket</artifactId>
    <version>1.0.0</version>
</dependency>
```
