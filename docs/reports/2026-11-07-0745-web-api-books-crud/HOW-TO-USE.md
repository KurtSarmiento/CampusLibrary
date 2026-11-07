# How to Use: Web API Books CRUD + Swagger

## Prerequisites

### Verified Working ✅
- .NET SDK 8.0 or higher (you have 10.0.403)
- SQL Server LocalDB (`(localdb)\MSSQLLocalDB`)
- CampusLibraryDb database created (from Milestone 2)
- WebApi project builds successfully

### What's Available Now
- ✅ GET /api/books - List all books
- ✅ GET /api/books/{id} - Get a specific book
- ✅ POST /api/books - Create a new book
- ✅ PUT /api/books/{id} - Update a book
- ✅ DELETE /api/books/{id} - Delete a book
- ✅ Swagger UI for API documentation
- ✅ CORS configured for Android emulator
- ❌ No Members endpoints yet (Milestone 4)
- ❌ No Loans endpoints yet (Milestone 4)

## Step-by-Step: Working with the API

### 1. Start the API

```bash
# From the solution root directory
dotnet run --project src/CampusLibrary.WebApi

# Expected Output:
# Now listening on: http://localhost:5238
# Swagger UI: http://localhost:5238/swagger
```

### 2. Access Swagger UI

Open your browser to:
`http://localhost:5238/swagger`

You should see all available endpoints:
- GET /api/books
- GET /api/books/{id}
- POST /api/books
- PUT /api/books/{id}
- DELETE /api/books/{id}

### 3. Test the Endpoints

#### GET /api/books

**Description:** Get all books

**Request:**
```bash
curl -X GET "http://localhost:5238/api/books" -H "accept: application/json"
```

**Expected Response (200 OK):**
```json
[
    {
        "id": 1,
        "isbn": "9780134685991",
        "title": "Effective Java",
        "author": "Joshua Bloch",
        "publishedYear": 2018
    },
    {
        "id": 2,
        "isbn": "9780132350884",
        "title": "Clean Code",
        "author": "Robert C. Martin",
        "publishedYear": 2008
    },
    // ... all 5 books
]
```

#### GET /api/books/{id}

**Description:** Get a specific book

**Request:**
```bash
curl -X GET "http://localhost:5238/api/books/1" -H "accept: application/json"
```

**Expected Response (200 OK):**
```json
{
    "id": 1,
    "isbn": "9780134685991",
    "title": "Effective Java",
    "author": "Joshua Bloch",
    "publishedYear": 2018
}
```

**Error Case:**
```bash
curl -X GET "http://localhost:5238/api/books/999" -H "accept: application/json"
```

**Expected Response (404 Not Found):**
```json
{
    "message": "Book with ID 999 not found"
}
```

#### POST /api/books

**Description:** Create a new book

**Request:**
```bash
curl -X POST "http://localhost:5238/api/books" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"isbn\":\"9780134685992\",\"title\":\"Effective Java 2nd Edition\",\"author\":\"Joshua Bloch\",\"publishedYear\":2018}"
```

**Expected Response (201 Created):**
```json
{
    "id": 6,
    "isbn": "9780134685992",
    "title": "Effective Java 2nd Edition",
    "author": "Joshua Bloch",
    "publishedYear": 2018
}
```

**Location Header:** `http://localhost:5238/api/books/6`

**Error Case (Duplicate ISBN):**
```bash
curl -X POST "http://localhost:5238/api/books" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"isbn\":\"9780134685991\",\"title\":\"Duplicate\",\"author\":\"Test\",\"publishedYear\":2023}"
```

**Expected Response (409 Conflict):**
```json
{
    "message": "A book with ISBN 9780134685991 already exists"
}
```

**Error Case (Invalid Data):**
```bash
curl -X POST "http://localhost:5238/api/books" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"isbn\":\"123\",\"title\":\"\",\"author\":\"\",\"publishedYear\":0}"
```

**Expected Response (400 Bad Request):**
```json
{
    "errors": {
        "ISBN": [
            "The field ISBN must be a string or array type with a minimum length of '13' and a maximum length of '13'."
        ],
        "Title": [
            "The Title field is required."
        ]
    },
    "type": "https://tools.ietf.org/html/rfc7231#section-6.5.1",
    "title": "One or more validation errors occurred.",
    "status": 400,
    "traceId": "00-abc123..."
}
```

#### PUT /api/books/{id}

**Description:** Update a book

**Request:**
```bash
curl -X PUT "http://localhost:5238/api/books/6" -H "accept: */*" -H "Content-Type: application/json" -d "{\"id\":6,\"isbn\":\"9780134685992\",\"title\":\"Effective Java 2nd Edition (Updated)\",\"author\":\"Joshua Bloch\",\"publishedYear\":2018}"
```

**Expected Response (204 No Content)**

**Error Case (ID Mismatch):**
```bash
curl -X PUT "http://localhost:5238/api/books/6" -H "accept: */*" -H "Content-Type: application/json" -d "{\"id\":999,\"isbn\":\"9780134685992\",\"title\":\"Mismatch\",\"author\":\"Test\",\"publishedYear\":2023}"
```

**Expected Response (400 Bad Request):**
```json
{
    "message": "Book ID in URL does not match ID in request body"
}
```

#### DELETE /api/books/{id}

**Description:** Delete a book

**Request:**
```bash
curl -X DELETE "http://localhost:5238/api/books/6" -H "accept: */*"
```

**Expected Response (204 No Content)**

**Error Case (Non-existent book):**
```bash
curl -X DELETE "http://localhost:5238/api/books/999" -H "accept: */*"
```

**Expected Response (404 Not Found):**
```json
{
    "message": "Book with ID 999 not found"
}
```

### 4. Test CORS for Android Emulator

**Request from Android Emulator:**
```bash
curl -X GET "http://10.0.2.2:5238/api/books" -H "accept: application/json"
```

**Expected Response (200 OK):**
```json
[
    // All 5 books
]
```

**If CORS fails:**
1. Check CORS policy in Program.cs
2. Verify Android emulator is using `http://10.0.2.2`
3. Restart the API

### 5. Access the API from Another Application

**Example: From Postman**
1. Open Postman
2. Create a new GET request to `http://localhost:5238/api/books`
3. Add header: `Accept: application/json`
4. Send the request

**Example: From JavaScript (Browser)**
```javascript
fetch('http://localhost:5238/api/books', {
    headers: {
        'Accept': 'application/json'
    }
})
.then(response => response.json())
.then(data => console.log(data));
```

### 6. Stop the API

Press `Ctrl+C` in the terminal where the API is running.

## Example: What You Can Do Now

### Query Books with JavaScript

```javascript
// Fetch all books
async function getBooks() {
    const response = await fetch('http://localhost:5238/api/books', {
        headers: {
            'Accept': 'application/json'
        }
    });
    const books = await response.json();
    console.log(books);
}

// Get a specific book
async function getBook(id) {
    const response = await fetch(`http://localhost:5238/api/books/${id}`, {
        headers: {
            'Accept': 'application/json'
        }
    });
    if (response.ok) {
        const book = await response.json();
        console.log(book);
    } else {
        console.error('Book not found');
    }
}
```

### Create a New Book with JavaScript

```javascript
async function createBook(bookData) {
    const response = await fetch('http://localhost:5238/api/books', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(bookData)
    });

    if (response.ok) {
        const createdBook = await response.json();
        console.log('Book created:', createdBook);
        // Get the Location header for the new book
        const location = response.headers.get('Location');
        console.log('Location:', location);
    } else {
        const error = await response.json();
        console.error('Error creating book:', error);
    }
}

// Example usage
createBook({
    isbn: '9780134685992',
    title: 'Effective Java 2nd Edition',
    author: 'Joshua Bloch',
    publishedYear: 2018
});
```

## Troubleshooting

### Issue: Swagger UI not loading

**Symptom:**
- Browser shows blank page or error when accessing `/swagger`

**Solution:**
1. Verify API is running
2. Check Program.cs for Swagger configuration
3. Ensure `app.UseSwagger()` and `app.UseSwaggerUI()` are in the pipeline
4. Restart the API

### Issue: CORS not working for Android emulator

**Symptom:**
- Android app gets CORS error when calling API

**Solution:**
1. Verify CORS policy in Program.cs
2. Check Android emulator is using `http://10.0.2.2`
3. Restart the API
4. Verify no typos in the origin URL

### Issue: 404 Not Found for all endpoints

**Symptom:**
- All endpoints return 404

**Solution:**
1. Verify `app.MapControllers()` is in Program.cs
2. Check controller route attributes
3. Verify controller is in the correct namespace
4. Restart the API

### Issue: Database connection errors

**Symptom:**
- API starts but endpoints return 500 errors

**Solution:**
1. Verify connection string in `appsettings.Development.json`
2. Check LocalDB is running: `sqllocaldb info`
3. Verify database exists: `dotnet ef database update`

### Issue: API won't start

**Symptom:**
- `dotnet run` fails with errors

**Solution:**
1. Verify WebApi project builds: `dotnet build src/CampusLibrary.WebApi`
2. Check for missing dependencies
3. Verify DbContext is registered in Program.cs
4. Check connection string in `appsettings.Development.json`

## Next Steps (Milestone 4)

Once you're ready for Milestone 4:

1. **Create MembersController** with CRUD endpoints:
   - GET /api/members
   - GET /api/members/{id}
   - POST /api/members
   - (No PUT/DELETE for members - keep it simple)

2. **Create LoansController** with borrow/return endpoints:
   - POST /api/loans (borrow)
   - POST /api/loans/{id}/return (return)

3. **Implement loan business rule**: A book can have only one open loan at a time

4. **Test all endpoints via Swagger**

5. **Verify CORS for Android emulator**

See CLAUDE.md section 6 for the full milestone sequence.
