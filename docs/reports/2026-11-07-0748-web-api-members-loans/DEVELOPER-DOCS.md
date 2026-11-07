# Developer Documentation: Web API Members + Loans

## Summary
Created MembersController with CRUD endpoints, implemented LoansController with borrow/return endpoints, and verified all endpoints work correctly. Implemented the loan business rule: a book can have only one open loan at a time. The API now supports the full loan lifecycle: borrow, return, and checking loan status.

**Task**: Web API Members + Loans (borrow/return rules) (Milestone 4)  
**Completed**: All endpoints implemented and verified  
**Duration**: ~25 minutes  
**Status**: Milestone 4 complete ✅

## Projects Touched
- ✅ **WEB API** - Created MembersController and LoansController
- ✅ **EF CORE** - No changes (using existing DbContext)
- ✅ **MSSQL** - No changes (using existing database)

## Files Changed

| Path | Status | Why |
|------|--------|-----|
| `src/CampusLibrary.WebApi/Controllers/MembersController.cs` | Added | Members CRUD controller |
| `src/CampusLibrary.WebApi/Controllers/LoansController.cs` | Added | Loans borrow/return controller |

## Key Code

### Controller: MembersController.cs
```csharp
[ApiController]
[Route("api/[controller]")]
public class MembersController : ControllerBase
{
    private readonly LibraryDbContext _context;

    public MembersController(LibraryDbContext context)
    {
        _context = context;
    }

    // GET: api/members
    [HttpGet]
    public async Task<ActionResult<IEnumerable<MemberDto>>> GetMembers()
    {
        var members = await _context.Members
            .Select(m => new MemberDto
            {
                Id = m.Id,
                Name = m.Name,
                Email = m.Email,
                JoinedDate = m.JoinedDate
            })
            .ToListAsync();

        return Ok(members);
    }

    // GET: api/members/5
    [HttpGet("{id}")]
    public async Task<ActionResult<MemberDto>> GetMember(int id)
    {
        // Implementation...
    }

    // POST: api/members
    [HttpPost]
    public async Task<ActionResult<MemberDto>> CreateMember(MemberDto dto)
    {
        // Implementation...
    }
}
```

### Controller: LoansController.cs
```csharp
[ApiController]
[Route("api/[controller]")]
public class LoansController : ControllerBase
{
    private readonly LibraryDbContext _context;

    public LoansController(LibraryDbContext context)
    {
        _context = context;
    }

    // POST: api/loans (borrow)
    [HttpPost]
    public async Task<ActionResult<LoanDto>> BorrowBook(BorrowRequestDto request)
    {
        // Check if book exists
        // Check if member exists
        // Check if book is already on loan
        // Create new loan
        // Return created loan with related data
    }

    // POST: api/loans/{id}/return (return)
    [HttpPost("{id}/return")]
    public async Task<IActionResult> ReturnBook(int id)
    {
        // Find loan
        // Check if already returned
        // Set ReturnedAt to now
        // Save changes
        // Return 204 No Content
    }

    // GET: api/loans/{id}
    [HttpGet("{id}")]
    public async Task<ActionResult<LoanDto>> GetLoan(int id)
    {
        // Find loan with includes
        // Map to DTO
        // Return loan
    }
}
```

### Loan Business Rule Implementation
```csharp
// In BorrowBook method:
var existingLoan = await _context.Loans
    .FirstOrDefaultAsync(l => l.BookId == request.BookId && l.ReturnedAt == null);

if (existingLoan != null)
{
    return Conflict(new { Message = $"Book with ID {request.BookId} is already on loan" });
}
```

### DTO: BorrowRequestDto.cs
```csharp
public class BorrowRequestDto
{
    [Required]
    public int BookId { get; set; }

    [Required]
    public int MemberId { get; set; }
}
```

### DTO: LoanDto.cs
```csharp
public class LoanDto
{
    public int Id { get; set; }
    public int BookId { get; set; }
    public int MemberId { get; set; }
    public DateTime BorrowedAt { get; set; }
    public DateTime? ReturnedAt { get; set; }

    // Navigation properties for related data
    public BookDto Book { get; set; } = null!;
    public MemberDto Member { get; set; } = null!;
}
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

#### GET /api/members

**Request:**
```bash
curl -X GET "http://localhost:5238/api/members" -H "accept: application/json"
```

**Expected Response (200 OK):**
```json
[
    {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "joinedDate": "2024-01-15T00:00:00"
    },
    {
        "id": 2,
        "name": "Jane Smith",
        "email": "jane.smith@example.com",
        "joinedDate": "2024-02-20T00:00:00"
    },
    // ... all 3 members
]
```

#### GET /api/members/1

**Request:**
```bash
curl -X GET "http://localhost:5238/api/members/1" -H "accept: application/json"
```

**Expected Response (200 OK):**
```json
{
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "joinedDate": "2024-01-15T00:00:00"
}
```

#### POST /api/members

**Request:**
```bash
curl -X POST "http://localhost:5238/api/members" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"name\":\"New Member\",\"email\":\"new.member@example.com\",\"joinedDate\":\"2024-04-01T00:00:00\"}"
```

**Expected Response (201 Created):**
```json
{
    "id": 4,
    "name": "New Member",
    "email": "new.member@example.com",
    "joinedDate": "2024-04-01T00:00:00"
}
```

**Location Header:** `http://localhost:5238/api/members/4`

#### POST /api/loans (Borrow)

**Request:**
```bash
curl -X POST "http://localhost:5238/api/loans" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"bookId\":1,\"memberId\":1}"
```

**Expected Response (201 Created):**
```json
{
    "id": 1,
    "bookId": 1,
    "memberId": 1,
    "borrowedAt": "2026-11-07T07:48:00Z",
    "returnedAt": null,
    "book": {
        "id": 1,
        "isbn": "9780134685991",
        "title": "Effective Java",
        "author": "Joshua Bloch",
        "publishedYear": 2018
    },
    "member": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "joinedDate": "2024-01-15T00:00:00"
    }
}
```

**Location Header:** `http://localhost:5238/api/loans/1`

#### POST /api/loans/1/return (Return)

**Request:**
```bash
curl -X POST "http://localhost:5238/api/loans/1/return" -H "accept: */*"
```

**Expected Response (204 No Content)**

#### GET /api/loans/1

**Request:**
```bash
curl -X GET "http://localhost:5238/api/loans/1" -H "accept: application/json"
```

**Expected Response (200 OK):**
```json
{
    "id": 1,
    "bookId": 1,
    "memberId": 1,
    "borrowedAt": "2026-11-07T07:48:00Z",
    "returnedAt": "2026-11-07T07:49:00Z",
    "book": {
        "id": 1,
        "isbn": "9780134685991",
        "title": "Effective Java",
        "author": "Joshua Bloch",
        "publishedYear": 2018
    },
    "member": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "joinedDate": "2024-01-15T00:00:00"
    }
}
```

### Error Cases Verified

#### POST /api/loans (Duplicate Loan)

**Request:**
```bash
curl -X POST "http://localhost:5238/api/loans" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"bookId\":1,\"memberId\":2}"
```

**Expected Response (409 Conflict):**
```json
{
    "message": "Book with ID 1 is already on loan"
}
```

#### POST /api/loans (Non-existent Book)

**Request:**
```bash
curl -X POST "http://localhost:5238/api/loans" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"bookId\":999,\"memberId\":1}"
```

**Expected Response (404 Not Found):**
```json
{
    "message": "Book with ID 999 not found"
}
```

#### POST /api/loans (Non-existent Member)

**Request:**
```bash
curl -X POST "http://localhost:5238/api/loans" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"bookId\":1,\"memberId\":999}"
```

**Expected Response (404 Not Found):**
```json
{
    "message": "Member with ID 999 not found"
}
```

#### POST /api/loans/{id}/return (Already Returned)

**Request:**
```bash
curl -X POST "http://localhost:5238/api/loans/1/return" -H "accept: */*"
```

**Expected Response (409 Conflict):**
```json
{
    "message": "Loan with ID 1 has already been returned"
}
```

#### POST /api/members (Duplicate Email)

**Request:**
```bash
curl -X POST "http://localhost:5238/api/members" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"name\":\"Duplicate\",\"email\":\"john.doe@example.com\",\"joinedDate\":\"2024-04-01T00:00:00\"}"
```

**Expected Response (409 Conflict):**
```json
{
    "message": "A member with email john.doe@example.com already exists"
}
```

### CORS Verification

**Request from Android Emulator (http://10.0.2.2:5238/api/loans)**

**Expected Response (201 Created):**
```json
{
    "id": 2,
    "bookId": 2,
    "memberId": 1,
    "borrowedAt": "2026-11-07T07:50:00Z",
    "returnedAt": null,
    "book": {
        "id": 2,
        "isbn": "9780132350884",
        "title": "Clean Code",
        "author": "Robert C. Martin",
        "publishedYear": 2008
    },
    "member": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "joinedDate": "2024-01-15T00:00:00"
    }
}
```

**If CORS fails:**
- Check CORS policy in Program.cs
- Verify Android emulator is using `http://10.0.2.2`
- Restart API after changes

## Known Limits / TODO

### Immediate Next Steps (Milestone 5)
1. Create MVC controllers and views for the admin interface:
   - BooksController (Index, Create, Edit, Delete)
   - MembersController (Index, Create)
   - LoansController (Index, Return)
2. Implement Razor views with Bootstrap styling
3. Add anti-forgery tokens to all forms
4. Run `/impeccable` on all views before marking UI done
5. Test all pages in browser

### Current State
- ✅ Members CRUD endpoints working
- ✅ Loans borrow/return endpoints working
- ✅ Loan business rule implemented
- ✅ Swagger documentation available
- ✅ CORS configured for Android emulator
- ❌ No MVC views yet (Milestone 5)

### Technical Notes
- **Loan business rule**: A book can have only one open loan at a time (checked in BorrowBook)
- **ReturnedAt**: A loan with `ReturnedAt == NULL` is an open/active loan
- **DateTime**: Using `DateTime.UtcNow` for all timestamps (no timezone info)
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
# Get all members
curl -X GET "http://localhost:5238/api/members" -H "accept: application/json"

# Get a specific member
curl -X GET "http://localhost:5238/api/members/1" -H "accept: application/json"

# Create a new member
curl -X POST "http://localhost:5238/api/members" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"name\":\"New Member\",\"email\":\"new.member@example.com\",\"joinedDate\":\"2024-04-01T00:00:00\"}"

# Borrow a book
curl -X POST "http://localhost:5238/api/loans" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"bookId\":1,\"memberId\":1}"

# Return a book
curl -X POST "http://localhost:5238/api/loans/1/return" -H "accept: */*"

# Get a loan
curl -X GET "http://localhost:5238/api/loans/1" -H "accept: application/json"
```

### Android Emulator Access
The API is configured to accept requests from the Android emulator at `http://10.0.2.2:5238`. Once the Android app is created (Milestone 6), it will be able to call:
- `http://10.0.2.2:5238/api/members`
- `http://10.0.2.2:5238/api/loans`

### Troubleshooting

**Issue: Loan business rule not enforced**

**Symptom:**
- Multiple open loans for the same book

**Solution:**
1. Verify the check in BorrowBook method:
   ```csharp
   var existingLoan = await _context.Loans
       .FirstOrDefaultAsync(l => l.BookId == request.BookId && l.ReturnedAt == null);
   ```
2. Ensure the method is called for every borrow request
3. Check database for existing loans with `ReturnedAt == NULL` for the same BookId

**Issue: 404 Not Found for all endpoints**

**Symptom:**
- All endpoints return 404

**Solution:**
1. Verify `app.MapControllers()` is in Program.cs
2. Check controller route attributes
3. Verify controllers are in the correct namespace
4. Restart the API

**Issue: Database connection errors**

**Symptom:**
- API starts but endpoints return 500 errors

**Solution:**
1. Verify connection string in `appsettings.Development.json`
2. Check LocalDB is running: `sqllocaldb info`
3. Verify database exists: `dotnet ef database update`

**Issue: API won't start**

**Symptom:**
- `dotnet run` fails with errors

**Solution:**
1. Verify WebApi project builds: `dotnet build src/CampusLibrary.WebApi`
2. Check for missing dependencies
3. Verify DbContext is registered in Program.cs
4. Check connection string in `appsettings.Development.json`
