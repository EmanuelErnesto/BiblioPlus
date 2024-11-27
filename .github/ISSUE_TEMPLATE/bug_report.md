---
name: Bug report
about: Create a report to help us improve
title: ''
labels: ''
assignees: ''

---

## Describe the bug
A clear and concise description of what the bug is. For example, "The API endpoint `GET /books` is returning a 500 error when trying to fetch a list of books."

## To Reproduce
Steps to reproduce the issue:

1. Use Postman (or any HTTP client like Insomnia/cURL)
2. Make a request to `POST /books` with the following body:
   ```json
   {
     "title": "New Book",
     "author": "Author Name"
   }
   ```
3. Observe the error message/response code

## Expected behavior
A clear and concise description of what you expected to happen. For example, "The POST /books endpoint should return a 201 Created status and the created bookModel in the response body."

## API Request Details

### Method
[GET, POST, PUT, DELETE, etc.]

### Endpoint
[e.g., /books, /users/{id}, etc.]

### Request Headers
```
Authorization: Bearer <your-token>
Content-Type: application/json
```

### Request Body
```json
{
  "title": "New Book",
  "author": "Author Name"
}
```

### Response Status Code
[e.g., 200, 400, 404, 500, etc.]

### Response Body
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred while processing the request."
}
```

## Screenshots
If applicable, add screenshots or screenshots of the error response.

## Environment
Please complete the following information:

- **API Version**: [e.g., 1.0.0]
- **OS**: [e.g., Windows 10, macOS 11.2, Linux]
- **HTTP Client**: [e.g., Postman, Insomnia, cURL, etc.]
- **Request Headers**: [Include any relevant headers that may affect the request]

## Additional context
Add any other context or logs that could help us reproduce the issue.
For example:
- Server-side logs
- Stack traces
- Unusual system behavior
