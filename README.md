# scribe-java-bitbucket

These classes makes it possible to use Bitbucket API from [Scribe](https://github.com/fernandezpablo85/scribe-java/), like this.

```java
OAuthService service = new ServiceBuilder()
                                  .provider(BitbucketApi20.class)
                                  .apiKey(BITBUCKET_API_KEY)
                                  .apiSecret(BITBUCKET_API_SECRET)
                                  .build();
```

The project is not available in any public Maven repository at the moment. To use it, clone this repository and run "mvn install"
from the root folder.

See the documentation for [Scribe](https://github.com/fernandezpablo85/scribe-java/) for more usage instructions.
