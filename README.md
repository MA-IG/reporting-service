# Reporting Server

A RESTful reporting server built with **Spring Boot** and **JasperReports**. It allows clients to manage report templates and generate PDF reports dynamically by providing report parameters and data through an API.

## Features
- **Report template management** — Upload, list, edit, and delete compiled JasperReports templates (`.jasper`).
- **Dynamic PDF generation** — Generate PDF documents from stored templates using custom parameters and data.
- **Web interface** — Manage templates, generate PDFs, browse generation history, and manage favorites through a simple Thymeleaf and Bootstrap interface.
- **Report regeneration** — Reopen a previous generation with its report template, parameters, and data prefilled for easy regeneration.
- **REST API** — Integrate report generation into external applications.

## Tech Stack

| Technology | Purpose                              |
|---|--------------------------------------|
| Java 21 | Programming language                 |
| Spring Boot | Application framework                |
| Spring Web MVC | REST API and web endpoints           |
| Spring Data JPA | Database access                      |
| H2 Database | Template metadata persistence        |
| JasperReports | Report processing and PDF generation |
| Thymeleaf | Server-rendered web interface        |
| Bootstrap | UI styling                           |
| Springdoc OpenAPI | Swagger API documentation            |
| Maven | Dependency management and build      |

## Getting Started

### Prerequisites

- Java 21 or compatible JDK
- Maven, or the Maven Wrapper included in the project
- Git (optional, for cloning the repository)

### Installation

Clone the repository:

```bash
git clone <repository-url>
cd <project-directory>
```

Build the application:

**Windows**

```powershell
.\mvnw.cmd clean package
```

**Linux / macOS**

```bash
./mvnw clean package
```

Run the application:

**Windows**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS**

```bash
./mvnw spring-boot:run
```

Alternatively, run the main Spring Boot application class from your IDE.

### Configuration

Configure the application in `src/main/resources/application.properties`:
Swagger uses default configuration

## Access the Application

Once the application is running on port `8080`:

| Resource | URL |
|---|---|
| Template management interface | [http://localhost:8080/](http://localhost:8080/) |
| Swagger UI | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| OpenAPI specification | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |
| H2 database console | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) |

For the H2 console, use the JDBC URL configured in `application.properties`, with username `sa` and the configured password.

## Managing Report Templates

Open the web interface to upload and manage compiled JasperReports templates.

Each template contains:

- **Name** — Unique name used to identify the report in API requests.
- **File** — Compiled JasperReports template with the `.jasper` extension.

Templates are stored on the filesystem, while their metadata is stored in the H2 database.

## PDF Generation API

### Generate a PDF report

**Endpoint**

```http
POST /api/reports/generate
```

**Request headers**

```http
Content-Type: application/json
```

**Request body**

```json
{
  "reportName": "employee-report",
  "parameters": {
    "title": "Employee Report",
    "department": "IT"
  },
  "data": [
    {
      "id": 1,
      "name": "Ahmed",
      "salary": 12000
    },
    {
      "id": 2,
      "name": "Sara",
      "salary": 15000
    }
  ]
}
```

### Request fields

| Field | Type | Description |
|---|---|---|
| `reportName` | String | Name of an existing report template |
| `parameters` | Object | Parameters declared in the JasperReports template |
| `data` | Array of objects | Records used to populate the report |

The keys in `parameters` must match the parameter names defined in the JasperReports template. The keys in each object in `data` must match its declared report fields.

### Response

On success, the API returns:

- **Status:** `200 OK`
- **Content-Type:** `application/pdf`
- **Content-Disposition:** `attachment; filename="employee-report.pdf"`
- **Body:** Generated PDF document

## JasperReports Version Compatibility

This project uses **JasperReports 6.20.6** to maintain compatibility with compiled `.jasper` templates generated using **Jaspersoft Studio 6.20**.

**Important:** Compiled `.jasper` files may not be compatible across different JasperReports versions. Ensure that your report templates are compiled using a compatible version of Jaspersoft Studio and JasperReports.

Or change jasperReports version in your pom.xml file

## Security Considerations

Before deploying to production:

- Protect template management and the H2 console with appropriate access controls.
- Restrict API access to authorized clients.
- Validate uploaded files and enforce file size limits.
- Avoid exposing internal filesystem paths and exception details.
- Configure persistent database storage and backups.
- Store sensitive configuration outside the repository.
- Consider authentication, authorization, rate limiting, and request-size limits for the PDF generation endpoint.


