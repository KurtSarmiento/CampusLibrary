# Spec: CampusLibrary.WebMvc (Admin Web Interface)

## Objective

Build a web-based admin interface for librarians to manage the campus library. This ASP.NET Core MVC app uses Razor views and Bootstrap for the UI, and connects directly to the `CampusLibrary.Data` layer (same `LibraryDbContext` as the API). Librarians can manage books (CRUD), view/create members, and manage loans (view open loans, mark as returned).

**Users**: Librarians, library staff.

**Success**: The web app runs, displays Bootstrap-styled pages, allows CRUD operations on books, member creation, and loan management with proper validation and anti-forgery tokens.

## Tech Stack

- .NET 8 SDK
- ASP.NET Core MVC with Razor views
- Bootstrap 5 (ships with MVC template)
- NuGet packages:
  - `Microsoft.EntityFrameworkCore.SqlServer` (8.x)
  - `Microsoft.EntityFrameworkCore.Design` (8.x, for migrations)
- Project reference: `CampusLibrary.Data`

## Commands

```bash
# Restore packages
dotnet restore src/CampusLibrary.WebMvc

# Build
dotnet build src/CampusLibrary.WebMvc

# Run (Development mode)
dotnet run --project src/CampusLibrary.WebMvc

# Watch mode (auto-reload on file changes)
dotnet watch --project src/CampusLibrary.WebMvc
```

## Project Structure

```
src/CampusLibrary.WebMvc/
├── CampusLibrary.WebMvc.csproj       → MVC project, .NET 8
├── Program.cs                         → Minimal hosting, DI setup
├── appsettings.json                   → Base settings
├── appsettings.Development.json       → Connection string
├── Controllers/
│   ├── BooksController.cs             → Books CRUD (Index/Create/Edit/Delete)
│   ├── MembersController.cs           → Members list/create
│   └── LoansController.cs             → Loans list (open), return action
├── Views/
│   ├── Shared/
│   │   ├── _Layout.cshtml             → Bootstrap layout with nav
│   │   └── _ValidationScriptsPartial.cshtml
│   ├── Books/
│   │   ├── Index.cshtml               → Books list table
│   │   ├── Create.cshtml              → Create book form
│   │   ├── Edit.cshtml                → Edit book form
│   │   └── Delete.cshtml              → Delete confirmation
│   ├── Members/
│   │   ├── Index.cshtml               → Members list table
│   │   └── Create.cshtml              → Create member form
│   └── Loans/
│       └── Index.cshtml               → Open loans table with return button
├── Models/                            → View models (if needed)
├── wwwroot/                           → Static files (Bootstrap, CSS, JS)
└── Properties/
    └── launchSettings.json            → Ports, environment
```

## Code Style

Controller example showing validation, anti-forgery, and EF access:

```csharp
namespace CampusLibrary.WebMvc.Controllers;

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
        if (ModelState.IsValid)
        {
            _context.Books.Add(book);
            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(Index));
        }
        return View(book);
    }

    // POST: Books/Delete/5
    [HttpPost, ActionName("Delete")]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> DeleteConfirmed(int id)
    {
        var book = await _context.Books.FindAsync(id);
        if (book != null)
        {
            _context.Books.Remove(book);
            await _context.SaveChangesAsync();
        }
        return RedirectToAction(nameof(Index));
    }
}
```

View example (Create.cshtml) showing tag helpers and validation:

```cshtml
@model Book

<h2>Create Book</h2>

<form asp-action="Create" method="post">
    <div class="mb-3">
        <label asp-for="ISBN" class="form-label"></label>
        <input asp-for="ISBN" class="form-control" />
        <span asp-validation-for="ISBN" class="text-danger"></span>
    </div>
    <div class="mb-3">
        <label asp-for="Title" class="form-label"></label>
        <input asp-for="Title" class="form-control" />
        <span asp-validation-for="Title" class="text-danger"></span>
    </div>
    <button type="submit" class="btn btn-primary">Create</button>
    <a asp-action="Index" class="btn btn-secondary">Cancel</a>
</form>

@section Scripts {
    @await Html.PartialAsync("_ValidationScriptsPartial")
}
```

**Conventions**:
- Controllers in `Controllers/` folder, suffixed with `Controller`
- Views in `Views/<ControllerName>/` folder
- Use tag helpers: `asp-action`, `asp-for`, `asp-validation-for`
- Bootstrap classes: `form-control`, `btn btn-primary`, `table`, `mb-3`
- `[ValidateAntiForgeryToken]` on every POST action
- Check `ModelState.IsValid` before saving
- `RedirectToAction(nameof(Index))` after successful POST
- Use EF entities directly (no DTOs needed for MVC views)

## Testing Strategy

**Unit tests**: Not included in initial milestone.

**Integration verification**:
1. Build succeeds: `dotnet build src/CampusLibrary.WebMvc`
2. Run app: `dotnet run --project src/CampusLibrary.WebMvc`
3. Open browser to `http://localhost:<port>`
4. Manual UI tests:
   - `/Books` shows 5 seeded books in table
   - `/Books/Create` form validates (required fields, max lengths), creates book
   - `/Books/Edit/1` form pre-fills, saves changes
   - `/Books/Delete/1` shows confirmation, deletes book
   - `/Members` shows 3 seeded members
   - `/Members/Create` creates member with validation
   - `/Loans` shows only open loans (ReturnedAt == null)
   - Return button on loan POSTs to `/Loans/Return/{id}`, sets ReturnedAt
5. Run `/impeccable` on all views before marking UI done

## Boundaries

**Always do**:
- Use `[ValidateAntiForgeryToken]` on every POST/PUT/DELETE action
- Check `ModelState.IsValid` before saving
- Use tag helpers (`asp-for`, `asp-action`, etc.)
- Show validation messages with `asp-validation-for`
- Use Bootstrap classes (no custom CSS unless required)
- Run `dotnet build` and manual browser test after every change
- Run `/impeccable` on views before calling UI task done

**Ask first**:
- Adding authentication/authorization (Identity, roles, login)
- Changing layout or Bootstrap theme
- Adding JavaScript beyond validation scripts
- Adding search/filter/pagination features

**Never do**:
- Skip anti-forgery tokens on POST actions
- Hard-code connection strings in Program.cs
- Create DTOs (use EF entities directly in views)
- Skip validation messages in forms
- Commit secrets

## Success Criteria

- [ ] `CampusLibrary.WebMvc.csproj` builds without errors
- [ ] App runs with `dotnet run --project src/CampusLibrary.WebMvc`
- [ ] Pages implemented:
  - **Books/Index**: Table of all books (ISBN, Title, Author, Year), Edit/Delete links
  - **Books/Create**: Form with validation, creates book, redirects to Index
  - **Books/Edit/{id}**: Pre-filled form, updates book, redirects to Index
  - **Books/Delete/{id}**: Confirmation page, POST deletes book
  - **Members/Index**: Table of all members (Name, Email, JoinedDate)
  - **Members/Create**: Form with validation, creates member
  - **Loans/Index**: Table of open loans (Book title, Member name, BorrowedAt), Return button
  - **Loans/Return/{id}**: POST action sets ReturnedAt to current time
- [ ] All forms use tag helpers and show validation messages
- [ ] All POST actions have `[ValidateAntiForgeryToken]`
- [ ] Bootstrap styling applied (tables, forms, buttons)
- [ ] Navigation links in `_Layout.cshtml` (Books, Members, Loans)
- [ ] `/impeccable` run on all views (no findings or all fixed)

## Open Questions

**ASSUMPTIONS I'M MAKING:**

1. **No authentication**: Anyone can access all pages (auth is milestone 8+ or optional)
2. **No borrow action in MVC**: Borrowing happens via Android app or API (MVC only does returns)
3. **Loans/Index shows open loans only**: Filter `ReturnedAt == null`
4. **Return button**: POST to `/Loans/Return/{id}`, sets `ReturnedAt = DateTime.UtcNow`
5. **No member edit/delete**: Members are read-only after creation (keep it simple)
6. **No soft delete**: Delete actions hard-delete records via `context.Remove()`
7. **Bootstrap version**: Use whatever ships with the .NET 8 MVC template (likely Bootstrap 5.x)
8. **Port**: Auto-assigned by Kestrel (different from WebApi port)

→ **Correct me now or I'll proceed with these assumptions.**
