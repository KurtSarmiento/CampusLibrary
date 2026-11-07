# How to Use: Web API Members + Loans

## Prerequisites

### Verified Working ✅
- .NET SDK 8.0 or higher (you have 10.0.403)
- SQL Server LocalDB (`(localdb)\MSSQLLocalDB`)
- CampusLibraryDb database created (from Milestone 2)
- WebApi project builds successfully
- Books endpoints working (from Milestone 3)

### What's Available Now
- ✅ GET /api/members - List all members
- ✅ GET /api/members/{id} - Get a specific member
- ✅ POST /api/members - Create a new member
- ✅ POST /api/loans - Borrow a book
- ✅ POST /api/loans/{id}/return - Return a book
- ✅ GET /api/loans/{id} - Get loan details
- ✅ Loan business rule: A book can have only one open loan at a time
- ✅ Swagger UI for API documentation
- ✅ CORS configured for Android emulator
- ❌ No MVC views yet (Milestone 5)

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
- GET /api/members
- GET /api/members/{id}
- POST /api/members
- POST /api/loans
- POST /api/loans/{id}/return
- GET /api/loans/{id}

### 3. Test the Endpoints

#### GET /api/members

**Description:** Get all members

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

#### GET /api/members/{id}

**Description:** Get a specific member

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

**Error Case:**
```bash
curl -X GET "http://localhost:5238/api/members/999" -H "accept: application/json"
```

**Expected Response (404 Not Found):**
```json
{
    "message": "Member with ID 999 not found"
}
```

#### POST /api/members

**Description:** Create a new member

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

**Error Case (Duplicate Email):**
```bash
curl -X POST "http://localhost:5238/api/members" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"name\":\"Duplicate\",\"email\":\"john.doe@example.com\",\"joinedDate\":\"2024-04-01T00:00:00\"}"
```

**Expected Response (409 Conflict):**
```json
{
    "message": "A member with email john.doe@example.com already exists"
}
```

#### POST /api/loans (Borrow)

**Description:** Borrow a book

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

**Error Case (Book Already on Loan):**
```bash
curl -X POST "http://localhost:5238/api/loans" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"bookId\":1,\"memberId\":2}"
```

**Expected Response (409 Conflict):**
```json
{
    "message": "Book with ID 1 is already on loan"
}
```

**Error Case (Non-existent Book):**
```bash
curl -X POST "http://localhost:5238/api/loans" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"bookId\":999,\"memberId\":1}"
```

**Expected Response (404 Not Found):**
```json
{
    "message": "Book with ID 999 not found"
}
```

**Error Case (Non-existent Member):**
```bash
curl -X POST "http://localhost:5238/api/loans" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"bookId\":1,\"memberId\":999}"
```

**Expected Response (404 Not Found):**
```json
{
    "message": "Member with ID 999 not found"
}
```

#### POST /api/loans/{id}/return (Return)

**Description:** Return a book

**Request:**
```bash
curl -X POST "http://localhost:5238/api/loans/1/return" -H "accept: */*"
```

**Expected Response (204 No Content)**

**Error Case (Already Returned):**
```bash
curl -X POST "http://localhost:5238/api/loans/1/return" -H "accept: */*"
```

**Expected Response (409 Conflict):**
```json
{
    "message": "Loan with ID 1 has already been returned"
}
```

#### GET /api/loans/{id}

**Description:** Get loan details

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

**Error Case (Non-existent Loan):**
```bash
curl -X GET "http://localhost:5238/api/loans/999" -H "accept: application/json"
```

**Expected Response (404 Not Found):**
```json
{
    "message": "Loan with ID 999 not found"
}
```

### 4. Test CORS for Android Emulator

**Request from Android Emulator:**
```bash
curl -X POST "http://10.0.2.2:5238/api/loans" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"bookId\":2,\"memberId\":1}"
```

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
1. Check CORS policy in Program.cs
2. Verify Android emulator is using `http://10.0.2.2`
3. Restart the API
4. Verify no typos in the origin URL

### 5. Access the API from Another Application

**Example: From Postman**
1. Open Postman
2. Create a new POST request to `http://localhost:5238/api/loans`
3. Add headers:
   - `Accept: application/json`
   - `Content-Type: application/json`
4. Add body:
   ```json
   {
       "bookId": 1,
       "memberId": 1
   }
   ```
5. Send the request

**Example: From JavaScript (Browser)**
```javascript
// Borrow a book
async function borrowBook(bookId, memberId) {
    const response = await fetch('http://localhost:5238/api/loans', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ bookId, memberId })
    });

    if (response.ok) {
        const loan = await response.json();
        console.log('Book borrowed:', loan);
        // Get the Location header for the new loan
        const location = response.headers.get('Location');
        console.log('Location:', location);
    } else {
        const error = await response.json();
        console.error('Error borrowing book:', error);
    }
}

// Return a book
async function returnBook(loanId) {
    const response = await fetch(`http://localhost:5238/api/loans/${loanId}/return`, {
        method: 'POST',
        headers: {
            'Accept': '*/*'
        }
    });

    if (response.ok) {
        console.log('Book returned successfully');
    } else {
        const error = await response.json();
        console.error('Error returning book:', error);
    }
}
```

### 6. Stop the API

Press `Ctrl+C` in the terminal where the API is running.

## Example: What You Can Do Now

### Query Members with JavaScript

```javascript
// Fetch all members
async function getMembers() {
    const response = await fetch('http://localhost:5238/api/members', {
        headers: {
            'Accept': 'application/json'
        }
    });
    const members = await response.json();
    console.log(members);
}

// Get a specific member
async function getMember(id) {
    const response = await fetch(`http://localhost:5238/api/members/${id}`, {
        headers: {
            'Accept': 'application/json'
        }
    });
    if (response.ok) {
        const member = await response.json();
        console.log(member);
    } else {
        console.error('Member not found');
    }
}
```

### Create a New Member with JavaScript

```javascript
async function createMember(memberData) {
    const response = await fetch('http://localhost:5238/api/members', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(memberData)
    });

    if (response.ok) {
        const createdMember = await response.json();
        console.log('Member created:', createdMember);
        // Get the Location header for the new member
        const location = response.headers.get('Location');
        console.log('Location:', location);
    } else {
        const error = await response.json();
        console.error('Error creating member:', error);
    }
}

// Example usage
createMember({
    name: 'New Member',
    email: 'new.member@example.com',
    joinedDate: '2024-04-01T00:00:00'
});
```

### Borrow and Return Books with JavaScript

```javascript
// Borrow a book
async function borrowBook(bookId, memberId) {
    const response = await fetch('http://localhost:5238/api/loans', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ bookId, memberId })
    });

    if (response.ok) {
        const loan = await response.json();
        console.log('Book borrowed:', loan);
        return loan.id; // Return the loan ID for returning later
    } else {
        const error = await response.json();
        console.error('Error borrowing book:', error);
        return null;
    }
}

// Return a book
async function returnBook(loanId) {
    const response = await fetch(`http://localhost:5238/api/loans/${loanId}/return`, {
        method: 'POST',
        headers: {
            'Accept': '*/*'
        }
    });

    if (response.ok) {
        console.log('Book returned successfully');
    } else {
        const error = await response.json();
        console.error('Error returning book:', error);
    }
}

// Example usage
(async () => {
    const loanId = await borrowBook(1, 1);
    if (loanId) {
        // Wait 5 seconds then return the book
        setTimeout(() => returnBook(loanId), 5000);
    }
})();
```

## Troubleshooting

### Issue: Loan business rule not enforced

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
3. Verify controllers are in the correct namespace
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

## Next Steps (Milestone 5)

Once you're ready for Milestone 5:

1. **Create MVC controllers and views** for the admin interface:
   - BooksController (Index, Create, Edit, Delete)
   - MembersController (Index, Create)
   - LoansController (Index, Return)

2. **Implement Razor views** with Bootstrap styling

3. **Add anti-forgery tokens** to all forms

4. **Run `/impeccable`** on all views before marking UI done

5. **Test all pages** in browser

See CLAUDE.md section 6 for the full milestone sequence.
