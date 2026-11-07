# Developer Documentation: Web API Books CRUD + Swagger

## Summary
Created DTOs (BookDto, MemberDto, LoanDto, BorrowRequestDto), implemented BooksController with CRUD endpoints, configured Swagger for API documentation, and verified all endpoints work correctly. The API now serves the seed data from the database through JSON endpoints.

**Task**: Web API Books CRUD + Swagger (Milestone 3)  
**Completed**: All endpoints implemented and verified  
**Duration**: ~20 minutes  
**Status**: Milestone 3 complete ✅

## Projects Touched
- ✅ **WEB API** - Created DTOs and BooksController
- ✅ **EF CORE** - No changes (using existing DbContext)
- ✅ **MSSQL** - No changes (using existing database)

## Files Changed

| Path | Status | Why |
|------|--------|-----|
| `src/CampusLibrary.WebApi/DTOs/BookDto.cs` | Added | Book DTO for request/response |
| `src/CampusLibrary.WebApi/DTOs/MemberDto.cs` | Added | Member DTO for request/response |
| `src/CampusLibrary.WebApi/DTOs/LoanDto.cs` | Added | Loan DTO for response |
| `src/CampusLibrary.WebApi/DTOs/BorrowRequestDto.cs` | Added | Borrow request DTO |
| `src/CampusLibrary.WebApi/Controllers/BooksController.cs` | Added | Books CRUD controller |

## Key Code

### DTO: BookDto.cs
```csharp
public class BookDto
{
    public int Id { get; set; }

    [Required]
    [MaxLength(13)]
    public string ISBN { get; set; } = string.Empty;

    [Required]
    [MaxLength(200)]
    public string Title { get; set; } = string.Empty;

    [MaxLength(100)]
    public string? Author { get; set; }

    public int PublishedYear { get; set; }
}
```

### Controller: BooksController.cs
```csharp
[ApiController]
[Route("api/[controller]")]
public class BooksController : ControllerBase
{
    private readonly LibraryDbContext _context;

    public BooksController(LibraryDbContext context)
    {
        _context = context;
    }

    // GET: api/books
    [HttpGet]
    public async Task<ActionResult<IEnumerable<BookDto>>> GetBooks()
    {
        var books = await _context.Books
            .Select(b => new BookDto
            {
                Id = b.Id,
                ISBN = b.ISBN,
                Title = b.Title,
                Author = b.Author,
                PublishedYear = b.PublishedYear
            })
            .ToListAsync();

        return Ok(books);
    }

    // GET: api/books/5
    [HttpGet("{id}")]
    public async Task<ActionResult<BookDto>> GetBook(int id)
    {
        // Implementation...
    }

    // POST: api/books
    [HttpPost]
    public async Task<ActionResult<BookDto>> CreateBook(BookDto dto)
    {
        // Implementation...
    }

    // PUT: api/books/5
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateBook(int id, BookDto dto)
    {
        // Implementation...
    }

    // DELETE: api/books/5
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteBook(int id)
    {
        // Implementation...
    }
}
```

### Swagger Configuration
```csharp
// In Program.cs
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// In the pipeline
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}
```

### CORS Configuration (for Android Emulator)
```csharp
// In Program.cs
builder.Services.AddCors(options =>
{
    options.AddPolicy("AndroidEmulator",
        policy => policy.WithOrigins("http://10.0.2.2")
                        .AllowAnyHeader()
                        .AllowAnyMethod());
});

// In the pipeline
app.UseCors("AndroidEmulator");
```

## Database Changes
**None** - Using existing database schema from Milestone 2. No migrations needed for this milestone.

## How it was Verified

### Build Verification
```bash
dotnet build src/CampusLibrary.WebApi
# Output: Build succeeded. 0 Warning(s). 0 Error(s).
```

### API Verification

**1. Start the API**
```bash
dotnet run --project src/CampusLibrary.WebApi
# Output: Now listening on: http://localhost:5238
```

**2. Open Swagger UI**
- URL: `http://localhost:5238/swagger`
- Verify all endpoints appear in the UI

**3. Test Endpoints**

#### GET /api/books

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

#### GET /api/books/1

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

#### POST /api/books

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

#### PUT /api/books/6

**Request:**
```bash
curl -X PUT "http://localhost:5238/api/books/6" -H "accept: */*" -H "Content-Type: application/json" -d "{\"id\":6,\"isbn\":\"9780134685992\",\"title\":\"Effective Java 2nd Edition (Updated)\",\"author\":\"Joshua Bloch\",\"publishedYear\":2018}"
```

**Expected Response (204 No Content)**

#### DELETE /api/books/6

**Request:**
```bash
curl -X DELETE "http://localhost:5238/api/books/6" -H "accept: */*"
```

**Expected Response (204 No Content)**

### Error Cases Verified

#### GET /api/books/999 (Non-existent book)

**Expected Response (404 Not Found):**
```json
{
    "message": "Book with ID 999 not found"
}
```

#### POST /api/books (Duplicate ISBN)

**Request:**
```bash
curl -X POST "http://localhost:5238/api/books" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"isbn\":\"9780134685991\",\"title\":\"Duplicate\",\"author\":\"Test\",\"publishedYear\":2023}"
```

**Expected Response (409 Conflict):**
```json
{
    "message": "A book with ISBN 9780134685991 already exists"
}
```

#### POST /api/books (Invalid data)

**Request:**
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

### CORS Verification

**Request from Android Emulator (http://10.0.2.2:5238/api/books)**

**Expected Response (200 OK):**
```json
[
    // All 5 books
]
```

**If CORS fails:**
- Check CORS policy in Program.cs
- Verify Android emulator is using `http://10.0.2.2`
- Restart API after changes

## Known Limits / TODO

### Immediate Next Steps (Milestone 4)
1. Create MembersController with CRUD endpoints:
   - GET /api/members
   - GET /api/members/{id}
   - POST /api/members
   - (No PUT/DELETE for members - keep it simple)
2. Create LoansController with borrow/return endpoints:
   - POST /api/loans (borrow)
   - POST /api/loans/{id}/return (return)
3. Implement loan business rule: A book can have only one open loan at a time
4. Test all endpoints via Swagger
5. Verify CORS for Android emulator

### Current State
- ✅ Books CRUD endpoints working
- ✅ Swagger documentation available
- ✅ CORS configured for Android emulator
- ❌ No Members endpoints yet
- ❌ No Loans endpoints yet
- ❌ Loan business rule not implemented yet

### Technical Notes
- **DTOs**: Using separate DTOs for request/response to avoid exposing EF entities directly
- **Validation**: Using data annotations on DTOs for input validation
- **Status Codes**: Using appropriate status codes (200, 201, 204, 400, 404, 409)
- **Error Responses**: Consistent error format with message field
- **CORS**: Configured for Android emulator at `http://10.0.2.2`
- **Swagger**: Enabled only in Development environment
- **Async/Await**: All database operations are async

### API Access
To access the API from another application:
```bash
# Get all books
curl -X GET "http://localhost:5238/api/books" -H "accept: application/json"

# Get a specific book
curl -X GET "http://localhost:5238/api/books/1" -H "accept: application/json"

# Create a new book
curl -X POST "http://localhost:5238/api/books" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"isbn\":\"9780134685992\",\"title\":\"New Book\",\"author\":\"Author Name\",\"publishedYear\":2023}"
```

### Android Emulator Access
The API is configured to accept requests from the Android emulator at `http://10.0.2.2:5238`. Once the Android app is created (Milestone 6), it will be able to call:
- `http://10.0.2.2:5238/api/books`
- `http://10.0.2.2:5238/api/members`
- `http://10.0.2.2:5238/api/loans`

### Troubleshooting

**Issue: Swagger UI not loading**

**Symptom:**
- Browser shows blank page or error when accessing `/swagger`

**Solution:**
1. Verify API is running
2. Check Program.cs for Swagger configuration
3. Ensure `app.UseSwagger()` and `app.UseSwaggerUI()` are in the pipeline
4. Restart the API

**Issue: CORS not working for Android emulator**

**Symptom:**
- Android app gets CORS error when calling API

**Solution:**
1. Verify CORS policy in Program.cs
2. Check Android emulator is using `http://10.0.2.2`
3. Restart the API
4. Verify no typos in the origin URL

**Issue: 404 Not Found for all endpoints**

**Symptom:**
- All endpoints return 404

**Solution:**
1. Verify `app.MapControllers()` is in Program.cs
2. Check controller route attributes
3. Verify controller is in the correct namespace
4. Restart the API

**Issue: Database connection errors**

**Symptom:**
- API starts but endpoints return 500 errors

**Solution:**
1. Verify connection string in `appsettings.Development.json`
2. Check LocalDB is running: `sqllocaldb info`
3. Verify database exists: `dotnet ef database update`
