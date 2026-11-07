# How to Use: Web MVC Admin Pages

## Prerequisites

### Verified Working ✅
- .NET SDK 8.0 or higher (you have 10.0.403)
- SQL Server LocalDB (`(localdb)\MSSQLLocalDB`)
- CampusLibraryDb database created (from Milestone 2)
- WebMvc project builds successfully
- Web API endpoints working (from Milestones 3-4)

### What's Available Now
- ✅ Books CRUD pages (Index, Create, Edit, Delete)
- ✅ Members pages (Index, Create)
- ✅ Loans pages (Index, Return)
- ✅ Bootstrap styling applied
- ✅ Anti-forgery tokens on all forms
- ✅ Server-side validation
- ❌ No Android app yet (Milestone 6)

## Step-by-Step: Working with the Admin Interface

### 1. Start the MVC App

```bash
# From the solution root directory
dotnet run --project src/CampusLibrary.WebMvc

# Expected Output:
# Now listening on: http://localhost:5290
```

### 2. Access the Admin Interface

Open your browser to:
`http://localhost:5290`

You should see the MVC home page with navigation links to:
- Books
- Members  
- Loans

### 3. Manage Books

#### View All Books

1. Click **Books** in the navigation
2. You should see a table with 5 seeded books
3. Each row has **Edit** and **Delete** buttons

#### Create a New Book

1. Click **Create New** button
2. Fill in the form:
   - **ISBN**: 13-character ISBN (e.g., `9780134685992`)
   - **Title**: Book title (required)
   - **Author**: Author name (optional)
   - **Published Year**: Year (e.g., `2023`)
3. Click **Create**
4. You'll be redirected to the Books list with the new book

**Example:**
- ISBN: `9780134685992`
- Title: `Test-Driven Development`
- Author: `Kent Beck`
- Published Year: `2002`

#### Edit a Book

1. Click **Edit** on any book
2. Modify any fields
3. Click **Save**
4. You'll be redirected to the Books list with the changes

#### Delete a Book

1. Click **Delete** on any book
2. Confirm the deletion on the confirmation page
3. Click **Delete**
4. You'll be redirected to the Books list without the deleted book

### 4. Manage Members

#### View All Members

1. Click **Members** in the navigation
2. You should see a table with 3 seeded members
3. Each row shows Name, Email, and Joined Date

#### Create a New Member

1. Click **Create New** button
2. Fill in the form:
   - **Name**: Member name (required)
   - **Email**: Email address (required, unique)
   - **Joined Date**: Date (defaults to today if not provided)
3. Click **Create**
4. You'll be redirected to the Members list with the new member

**Example:**
- Name: `Alice Johnson`
- Email: `alice.johnson@example.com`
- Joined Date: `2024-04-15`

### 5. Manage Loans

#### View Open Loans

1. Click **Loans** in the navigation
2. You'll see a table with all currently borrowed books (initially empty)
3. Each row shows:
   - Book title and ISBN
   - Member name and email
   - Borrowed date/time
   - **Return** button

#### Borrow a Book (via API)

**Note:** Borrowing is done via the Web API, not the MVC interface.

```bash
# Using curl
curl -X POST "http://localhost:5238/api/loans" -H "Content-Type: application/json" -d "{\"bookId\":1,\"memberId\":1}"

# Using Swagger
# Open http://localhost:5238/swagger
# Execute POST /api/loans with bookId and memberId
```

After borrowing, refresh the Loans page to see the new loan.

#### Return a Book

1. Click **Return** button on any loan
2. The loan will be marked as returned
3. The loan will disappear from the Open Loans list

### 6. Test Validation

#### Duplicate ISBN

1. Try to create a book with ISBN `9780134685991` (already exists)
2. You'll see an error: **"A book with this ISBN already exists"**
3. The book will not be created

#### Duplicate Email

1. Try to create a member with email `john.doe@example.com` (already exists)
2. You'll see an error: **"A member with this email already exists"**
3. The member will not be created

#### Required Fields

1. Try to create a book without a Title
2. You'll see an error: **"The Title field is required"**
3. The book will not be created

### 7. Stop the MVC App

Press `Ctrl+C` in the terminal where the MVC app is running.

## Example: Complete Workflow

### Scenario: Add a New Book and Member, Then Borrow

**Step 1: Add a New Book**
1. Navigate to `http://localhost:5290/Books`
2. Click **Create New**
3. Fill in:
   - ISBN: `9780134685992`
   - Title: `Test-Driven Development`
   - Author: `Kent Beck`
   - Published Year: `2002`
4. Click **Create**
5. Verify the book appears in the list (6 books total)

**Step 2: Add a New Member**
1. Navigate to `http://localhost:5290/Members`
2. Click **Create New**
3. Fill in:
   - Name: `Alice Johnson`
   - Email: `alice.johnson@example.com`
   - Joined Date: `2024-04-15`
4. Click **Create**
5. Verify the member appears in the list (4 members total)

**Step 3: Borrow the Book (via API)**
```bash
# Start the API if not already running
dotnet run --project src/CampusLibrary.WebApi

# Borrow the book
curl -X POST "http://localhost:5238/api/loans" -H "Content-Type: application/json" -d "{\"bookId\":6,\"memberId\":4}"
```

**Step 4: View the Loan**
1. Navigate to `http://localhost:5290/Loans`
2. Verify the loan appears in the list
3. You should see:
   - Book: `Test-Driven Development (9780134685992)`
   - Member: `Alice Johnson (alice.johnson@example.com)`
   - Borrowed At: Current date/time
   - **Return** button

**Step 5: Return the Book**
1. Click **Return** button
2. Verify the loan disappears from the list

## Troubleshooting

### Issue: Pages not loading

**Symptom:**
- Browser shows blank page or error when accessing pages

**Solution:**
1. Verify MVC app is running: `dotnet run --project src/CampusLibrary.WebMvc`
2. Check Program.cs for proper configuration
3. Ensure controllers are in the correct namespace
4. Restart the MVC app

### Issue: Forms not submitting

**Symptom:**
- Form submission fails with anti-forgery token error

**Solution:**
1. Verify anti-forgery token is included in form
2. Check JavaScript is enabled in browser
3. Verify form has `[ValidateAntiForgeryToken]` attribute
4. Restart the MVC app

### Issue: Validation errors not showing

**Symptom:**
- Form submits without validation errors

**Solution:**
1. Verify `asp-validation-for` is in the view
2. Check `@section Scripts` includes `_ValidationScriptsPartial`
3. Verify data annotations on entity classes
4. Restart the MVC app

### Issue: Database connection errors

**Symptom:**
- MVC app starts but pages show errors

**Solution:**
1. Verify connection string in `appsettings.Development.json`
2. Check LocalDB is running: `sqllocaldb info`
3. Verify database exists: `dotnet ef database update -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi`

### Issue: MVC won't start

**Symptom:**
- `dotnet run` fails with errors

**Solution:**
1. Verify WebMvc project builds: `dotnet build src/CampusLibrary.WebMvc`
2. Check for missing dependencies
3. Verify DbContext is registered in Program.cs
4. Check connection string in `appsettings.Development.json`

### Issue: Books/Members don't appear

**Symptom:**
- Tables are empty

**Solution:**
1. Verify database has seed data: Query database with SSMS or Azure Data Studio
2. Check connection string points to correct database
3. Verify migrations applied: `dotnet ef migrations list -p src/CampusLibrary.Data -s src/CampusLibrary.WebApi`

### Issue: Loans page is empty

**Symptom:**
- No loans appear even after borrowing via API

**Solution:**
1. Verify API is running: `dotnet run --project src/CampusLibrary.WebApi`
2. Check loan was created successfully via Swagger: `http://localhost:5238/swagger`
3. Query database directly: `SELECT * FROM Loans WHERE ReturnedAt IS NULL`
4. Refresh the Loans page in browser

## Next Steps (Milestone 6)

Once you're ready for Milestone 6:

1. **Create Android project** with Kotlin and Jetpack Compose
2. **Configure AndroidManifest.xml** with INTERNET permission
3. **Create Retrofit client** to call the Web API
4. **Implement BookListScreen** to display books from API
5. **Implement BookDetailScreen** with Borrow button
6. **Implement MyLoansScreen** to show user's active loans
7. **Test app on Android emulator**
8. **Verify CORS** for Android emulator

See CLAUDE.md section 6 for the full milestone sequence.
