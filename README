# Apply Digital BE Challenge.

This project provides API services and a scheduler service. The services are built and run using `docker-compose` for easy deployment and local development.

---

## Table of Contents
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Scheduler Service](#scheduler-service)
  - [How the Scheduler Works](#how-the-scheduler-works)
  - [Accessing the Database](#accessing-the-database)
  - [Viewing Logs for the Scheduler Service](#viewing-logs-for-the-scheduler-service)
- [Accessing Swagger Docs](#accessing-swagger-docs)
- [Consuming the API](#consuming-the-api)
  - [Obtaining an Access Token](#obtaining-an-access-token)
  - [Using the API](#using-the-api)

---

## Prerequisites

Make sure you have the following installed:
- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)

---

## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/reigncl/Gustavo-DM-JAVA-BE.git
   ```

2. Build and start the services using Docker Compose:

   ```bash
   cd Gustavo-DM-JAVA-BE
   docker-compose up --build
   ```

   This will:
   - Build the **api-services** and **scheduler-service** images.
   - Start the containers for the API services and the scheduler.

3. The services should now be up and running on `localhost`.

---

## Scheduler Service

The **scheduler-service** periodically fetches articles from the **Algolia public API** and persists them into the **PostgreSQL** database. This service runs as a separate container and depends on the **PostgreSQL** container for database access.

### How the Scheduler Works

1. The **scheduler-service** fetches data from the Algolia public API at regular intervals (controlled by the `SCHEDULER_FETCH_RATE` environment variable, set to `60000` milliseconds or 1 minute in the `docker-compose.yml` configuration).
2. The fetched articles are stored in the `algolia_articles` table in the PostgreSQL database.
3. The articles are saved as `AlgoliaArticleEntity` objects, and associated tags are stored in a join table `article_tags`, which links the articles to tags in the `algolia_tags` table.

---

### Accessing the Database

To view or query the data persisted by the **scheduler-service**, follow these steps:

1. **Access the PostgreSQL Database**:
   - Use any PostgreSQL client (e.g., pgAdmin, DBeaver, or the command-line `psql` tool).
   - The database connection details are as follows:
     - **Host**: `localhost`
     - **Port**: `5432`
     - **Database Name**: `apply_digital_db`
     - **Username**: `user`
     - **Password**: `password`

---

### Viewing Logs for the Scheduler Service

To view the logs of the **scheduler-service** (e.g., to check if it is correctly fetching and persisting articles), use the following Docker command:

```bash
docker logs scheduler-service
```

This will display logs from the scheduler-service container, including information about fetched articles and any errors.

---

## Accessing Swagger Docs

The Swagger documentation for the API is automatically available once the services are running.

Open your browser and navigate to:

[http://localhost:8080/api/docs](http://localhost:8080/api/docs)

This will open the Swagger UI, where you can explore the available API endpoints and interact with them directly.

---

## Consuming the API

### Obtaining an Access Token

The API requires an access token to authenticate requests. To obtain this token, send a POST request to the authentication service.

Example using `curl` (replace `your-auth-server-url` with the actual auth server URL):

```bash
curl -X POST http://localhost:8080/oauth2/token \
     -d "grant_type=client_credentials" \
     -u test-client:secret
```

This will return a response with the access token, like so:

```json
{
  "access_token": "your-access-token",
  "token_type": "Bearer",
  "expires_in": 300
}
```

##Consuming the API services
To make a request to a protected API endpoint, include the access token in the Authorization header like this:

To obtain the list of articles:

curl -X GET http://localhost:8080/api/articles -H "Authorization: Bearer your-access-token"

To change the current page of the set of results you can use the page and size path params:

curl -X GET http://localhost:8080/api/articles?page=1&size=5 -H "Authorization: Bearer your-access-token"

To filter the articles you can use and combine the author, tags, title, month path params like this:

curl -X GET http://localhost:8080/api/articles?author=myauthor&month=April, -H "Authorization: Bearer your-access-token"

To delete an article (through the objectId property of the article to be deleted):

curl -X DELETE http://localhost:8080/api/articles/{objectId}, -H "Authorization: Bearer your-access-token"

Or you can access the swagger documentation, click in the Authorize button, and then paste the access_token we just obtained. Then you'll be able to consume the different services methods.

### Accessing Swagger for API Interaction:

Alternatively, you can access the Swagger documentation, click the **Authorize** button, and paste the `access_token` obtained earlier. This allows you to interact with the API methods directly from the Swagger UI.

---
