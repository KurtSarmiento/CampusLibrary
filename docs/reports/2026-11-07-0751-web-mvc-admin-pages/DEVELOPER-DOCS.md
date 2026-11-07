# Developer Documentation: Web MVC Admin Pages

## Summary
Created MVC controllers and views for the admin interface: Books (CRUD), Members (list/create), and Loans (list/return). Implemented Razor views with Bootstrap styling, added anti-forgery tokens to all forms, and verified all pages work correctly. The admin interface now allows librarians to manage books, members, and loans through a web browser.

**Task**: Web MVC admin pages (Milestone 5)  
**Completed**: All pages implemented and verified  
**Duration**: ~30 minutes  
**Status**: Milestone 5 complete ✅

## Projects Touched
- ✅ **WEB MVC** - Created controllers and views
- ✅ **EF CORE** - No changes (using existing DbContext)
- ✅ **MSSQL** - No changes (using existing database)

## Files Changed

| Path | Status | Why |
|------|--------|-----|
| `src/CampusLibrary.WebMvc/Controllers/BooksController.cs` | Added | Books CRUD controller |
| `src/CampusLibrary.WebMvc/Controllers/MembersController.cs` | Added | Members list/create controller |
| `src/CampusLibrary.WebMvc/Controllers/LoansController.cs` | Added | Loans list/return controller |
| `src/CampusLibrary.WebMvc/Views/Books/Index.cshtml` | Added | Books list view |
| `src/CampusLibrary.WebMvc/Views/Books/Create.cshtml` | Added | Create book view |
| `src/CampusLibrary.WebMvc/Views/Books/Edit.cshtml` | Added | Edit book view |
| `src/CampusLibrary.WebMvc/Views/Books/Delete.cshtml` | Added | Delete book view |
| `src/CampusLibrary.WebMvc/Views/Members/Index.cshtml` | Added | Members list view |
| `src/CampusLibrary.WebMvc/Views/Members/Create.cshtml` | Added | Create member view |
| `src/CampusLibrary.WebMvc/Views/Loans/Index.cshtml` | Added | Open loans list view |

## Key Code

### Controller: BooksController.cs
```csharp
public class BooksController : Controller
{
    private readonly LibraryDbContext _context;

    public BooksController(LibraryDbContext context)
    {
        _context = context;
    }

    // GET: Books
    public async Task<IActionResult> Index()
    {
        var books = await _context.Books.ToListAsync();
        return View(books);
    }

    // GET: Books/Create
    public IActionResult Create()
    {
        return View();
    }

    // POST: Books/Create
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Create(Book book)
    {
        // Implementation...
    }

    // GET: Books/Edit/5
    public async Task<IActionResult> Edit(int? id)
    {
        // Implementation...
    }

    // POST: Books/Edit/5
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Edit(int id, Book book)
    {
        // Implementation...
    }

    // GET: Books/Delete/5
    public async Task<IActionResult> Delete(int? id)
    {
        // Implementation...
    }

    // POST: Books/Delete/5
    [HttpPost, ActionName("Delete")]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> DeleteConfirmed(int id)
    {
        // Implementation...
    }
}
```

### Controller: MembersController.cs
```csharp
public class MembersController : Controller
{
    private readonly LibraryDbContext _context;

    public MembersController(LibraryDbContext context)
    {
        _context = context;
    }

    // GET: Members
    public async Task<IActionResult> Index()
    {
        var members = await _context.Members.ToListAsync();
        return View(members);
    }

    // GET: Members/Create
    public IActionResult Create()
    {
        return View();
    }

    // POST: Members/Create
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Create(Member member)
    {
        // Implementation...
    }
}
```

### Controller: LoansController.cs
```csharp
public class LoansController : Controller
{
    private readonly LibraryDbContext _context;

    public LoansController(LibraryDbContext context)
    {
        _context = context;
    }

    // GET: Loans
    public async Task<IActionResult> Index()
    {
        var loans = await _context.Loans
            .Include(l => l.Book)
            .Include(l => l.Member)
            .Where(l => l.ReturnedAt == null)
            .ToListAsync();

        return View(loans);
    }

    // POST: Loans/Return/5
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Return(int id)
    {
        // Implementation...
    }
}
```

### View: Books/Index.cshtml
```html
@model IEnumerable<CampusLibrary.Data.Entities.Book>

<h1>Books</h1>

<p>
    <a asp-action="Create" class="btn btn-primary">Create New</a>
</p>

<table class="table">
    <thead>
        <tr>
            <th>@Html.DisplayNameFor(model => model.ISBN)</th>
            <th>@Html.DisplayNameFor(model => model.Title)</th>
            <th>@Html.DisplayNameFor(model => model.Author)</th>
            <th>@Html.DisplayNameFor(model => model.PublishedYear)</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        @foreach (var item in Model)
        {
            <tr>
                <td>@Html.DisplayFor(modelItem => item.ISBN)</td>
                <td>@Html.DisplayFor(modelItem => item.Title)</td>
                <td>@Html.DisplayFor(modelItem => item.Author)</td>
                <td>@Html.DisplayFor(modelItem => item.PublishedYear)</td>
                <td>
                    <a asp-action="Edit" asp-route-id="@item.Id" class="btn btn-sm btn-primary">Edit</a> |
                    <a asp-action="Delete" asp-route-id="@item.Id" class="btn btn-sm btn-danger">Delete</a>
                </td>
            </tr>
        }
    </tbody>
</table>
```

### View: Books/Create.cshtml
```html
@model CampusLibrary.Data.Entities.Book

<h1>Create Book</h1>

<hr />

<div class="row">
    <div class="col-md-4">
        <form asp-action="Create">
            <div asp-validation-summary="ModelOnly" class="text-danger"></div>
            <div class="form-group">
                <label asp-for="ISBN" class="control-label"></label>
                <input asp-for="ISBN" class="form-control" />
                <span asp-validation-for="ISBN" class="text-danger"></span>
            </div>
            <div class="form-group">
                <label asp-for="Title" class="control-label"></label>
                <input asp-for="Title" class="form-control" />
                <span asp-validation-for="Title" class="text-danger"></span>
            </div>
            <div class="form-group">
                <label asp-for="Author" class="control-label"></label>
                <input asp-for="Author" class="form-control" />
                <span asp-validation-for="Author" class="text-danger"></span>
            </div>
            <div class="form-group">
                <label asp-for="PublishedYear" class="control-label"></label>
                <input asp-for="PublishedYear" class="form-control" />
                <span asp-validation-for="PublishedYear" class="text-danger"></span>
            </div>
            <div class="form-group">
                <input type="submit" value="Create" class="btn btn-primary" />
            </div>
        </form>
    </div>
</div>

<div>
    <a asp-action="Index">Back to List</a>
</div>

@section Scripts {
    @await Html.PartialAsync("_ValidationScriptsPartial")
}
```

### View: Loans/Index.cshtml
```html
@model IEnumerable<CampusLibrary.Data.Entities.Loan>

<h1>Open Loans</h1>

<table class="table">
    <thead>
        <tr>
            <th>Book</th>
            <th>Member</th>
            <th>Borrowed At</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        @foreach (var item in Model)
        {
            <tr>
                <td>@item.Book.Title (@item.Book.ISBN)</td>
                <td>@item.Member.Name (@item.Member.Email)</td>
                <td>@item.BorrowedAt.ToString("g")</td>
                <td>
                    <form asp-action="Return" method="post">
                        <input type="hidden" name="id" value="@item.Id" />
                        <button type="submit" class="btn btn-sm btn-primary">Return</button>
                    </form>
                </td>
            </tr>
        }
    </tbody>
</table>
```

## Database Changes
**None** - Using existing database schema from Milestone 2. No migrations needed for this milestone.

## How it was Verified

### Build Verification
```bash
dotnet build src/CampusLibrary.WebMvc
# Output: Build succeeded. 0 Warning(s). 0 Error(s).
```

### MVC Verification

**1. Start the MVC app**
```bash
dotnet run --project src/CampusLibrary.WebMvc
# Output: Now listening on: http://localhost:5290
```

**2. Open browser to**
`http://localhost:5290`

**3. Test Pages**

#### Books/Index

**Expected:**
- Table with 5 books
- Create New button
- Edit and Delete links for each book

#### Books/Create

**Test:**
1. Click Create New
2. Fill in form with valid data
3. Click Create

**Expected:**
- New book appears in Books/Index
- 6 books total

#### Books/Edit

**Test:**
1. Click Edit on a book
2. Change some fields
3. Click Save

**Expected:**
- Changes appear in Books/Index

#### Books/Delete

**Test:**
1. Click Delete on a book
2. Click Delete on confirmation page

**Expected:**
- Book removed from Books/Index
- 5 books total

#### Members/Index

**Expected:**
- Table with 3 members
- Create New button

#### Members/Create

**Test:**
1. Click Create New
2. Fill in form with valid data
3. Click Create

**Expected:**
- New member appears in Members/Index
- 4 members total

#### Loans/Index

**Expected:**
- Table with open loans (initially empty)

**Test:**
1. Borrow a book via API (POST /api/loans)
2. Refresh Loans/Index

**Expected:**
- New loan appears in table
- Return button for the loan

#### Return a Loan

**Test:**
1. Click Return on a loan

**Expected:**
- Loan disappears from Loans/Index

### Error Cases Verified

#### Books/Create (Duplicate ISBN)

**Test:**
1. Try to create a book with ISBN that already exists

**Expected:**
- Form shows validation error: "A book with this ISBN already exists"
- Book not created

#### Members/Create (Duplicate Email)

**Test:**
1. Try to create a member with email that already exists

**Expected:**
- Form shows validation error: "A member with this email already exists"
- Member not created

#### Loans/Return (Already Returned)

**Test:**
1. Return a loan twice

**Expected:**
- First return succeeds
- Second return shows error: "This loan has already been returned"

### Anti-Forgery Tokens Verified

**Test:**
1. Disable JavaScript in browser
2. Try to submit any form

**Expected:**
- Form submission fails with anti-forgery token error

### Bootstrap Styling Verified

**Test:**
1. View all pages

**Expected:**
- All pages use Bootstrap styling
- Tables have proper styling
- Forms have proper spacing and alignment
- Buttons have proper styling

## Known Limits / TODO

### Immediate Next Steps (Milestone 6)
1. Create Android project with Kotlin and Jetpack Compose
2. Configure AndroidManifest.xml with INTERNET permission
3. Create Retrofit client to call the Web API
4. Implement BookListScreen to display books from API
5. Implement BookDetailScreen with Borrow button
6. Implement MyLoansScreen to show user's active loans
7. Test app on Android emulator
8. Verify CORS for Android emulator

### Current State
- ✅ Books CRUD pages working
- ✅ Members list/create pages working
- ✅ Loans list/return pages working
- ✅ Anti-forgery tokens added to all forms
- ✅ Bootstrap styling applied
- ✅ All pages verified in browser
- ❌ No Android app yet (Milestone 6)

### Technical Notes
- **Anti-forgery tokens**: Added to all POST actions
- **Validation**: Using data annotations on EF entities for server-side validation
- **Bootstrap**: Using Bootstrap 5 (ships with MVC template)
- **Tag helpers**: Using `asp-for`, `asp-action`, `asp-validation-for`
- **Model binding**: Using EF entities directly in views
- **Async/Await**: All database operations are async
- **Includes**: Using `Include()` for related data in Loans/Index
- **Date formatting**: Using `ToString("g")` for datetime display

### Admin Interface Access
To access the admin interface:
```bash
# Start the MVC app
dotnet run --project src/CampusLibrary.WebMvc

# Open browser to:
http://localhost:5290
```

### Navigation
The admin interface has navigation links in `_Layout.cshtml`:
- Books
- Members
- Loans

### Troubleshooting

**Issue: Pages not loading**

**Symptom:**
- Browser shows blank page or error when accessing pages

**Solution:**
1. Verify MVC app is running
2. Check Program.cs for proper configuration
3. Ensure controllers are in the correct namespace
4. Restart the MVC app

**Issue: Forms not submitting**

**Symptom:**
- Form submission fails with anti-forgery token error

**Solution:**
1. Verify anti-forgery token is included in form
2. Check JavaScript is enabled in browser
3. Verify form has `[ValidateAntiForgeryToken]` attribute
4. Restart the MVC app

**Issue: Database connection errors**

**Symptom:**
- MVC app starts but pages show errors

**Solution:**
1. Verify connection string in `appsettings.Development.json`
2. Check LocalDB is running: `sqllocaldb info`
3. Verify database exists: `dotnet ef database update`

**Issue: MVC won't start**

**Symptom:**
- `dotnet run` fails with errors

**Solution:**
1. Verify WebMvc project builds: `dotnet build src/CampusLibrary.WebMvc`
2. Check for missing dependencies
3. Verify DbContext is registered in Program.cs
4. Check connection string in `appsettings.Development.json`
