# Developer Documentation: Project Structure Setup

## Summary
Created the CampusLibrary solution with 3 .NET 8 projects (Data, WebApi, WebMvc) following the approved capability map and specifications. Configured project references, EF Core packages, connection strings, and CORS for Android emulator. All .NET projects build successfully.

**Task**: Create the solution and the 4 projects (Phase 1 - Milestone 1)  
**Completed**: .NET projects (3/4) - Android project pending due to missing Android SDK  
**Duration**: ~30 minutes  
**Status**: Phase 1 complete, Phase 2 deferred

## Projects Touched
- ✅ **EF CORE** - Created class library with EF Core 8.0.33 packages
- ✅ **WEB API** - Created Web API project with CORS and Swagger
- ✅ **WEB MVC** - Created MVC project with Bootstrap
- ❌ **ANDROID KOTLIN** - Deferred (Android SDK not available)
- ✅ **MSSQL** - Connection string configured for LocalDB

## Files Changed

| Path | Status | Why |
|------|--------|-----|
| `CampusLibrary.slnx` | Added | Solution file (.NET 10 XML format) |
| `src/CampusLibrary.Data/CampusLibrary.Data.csproj` | Added | EF Core class library, .NET 8 |
| `src/CampusLibrary.WebApi/CampusLibrary.WebApi.csproj` | Added | Web API project, .NET 8 |
| `src/CampusLibrary.WebApi/Program.cs` | Modified | Added CORS for Android emulator, configured for controllers |
| `src/CampusLibrary.WebApi/appsettings.Development.json` | Modified | Added LocalDB connection string |
| `src/CampusLibrary.WebMvc/CampusLibrary.WebMvc.csproj` | Added | MVC project, .NET 8 |
| `src/CampusLibrary.WebMvc/appsettings.Development.json` | Modified | Added LocalDB connection string |
| `tasks/todo.md` | Added | Task tracking document |
| `tasks/plan.md` | Added | Implementation plan |

## Key Code

### Connection String (WebApi & WebMvc)
```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Server=(localdb)\\MSSQLLocalDB;Database=CampusLibraryDb;Trusted_Connection=True;TrustServerCertificate=True;"
  }
}
```

### CORS Configuration (WebApi Program.cs)
```csharp
// CORS for Android emulator
builder.Services.AddCors(options =>
{
    options.AddPolicy("AndroidEmulator",
        policy => policy.WithOrigins("http://10.0.2.2")
                        .AllowAnyHeader()
                        .AllowAnyMethod());
});

// Later in pipeline:
app.UseCors("AndroidEmulator");
```

### Project References
- `CampusLibrary.WebApi` → references `CampusLibrary.Data`
- `CampusLibrary.WebMvc` → references `CampusLibrary.Data`

### NuGet Packages Installed
**CampusLibrary.Data:**
- `Microsoft.EntityFrameworkCore.SqlServer` (8.0.33)
- `Microsoft.EntityFrameworkCore.Design` (8.0.33)

**CampusLibrary.WebApi:**
- `Microsoft.EntityFrameworkCore.SqlServer` (8.0.33)
- `Swashbuckle.AspNetCore` (included in template)

**CampusLibrary.WebMvc:**
- `Microsoft.EntityFrameworkCore.SqlServer` (8.0.33)

## Database Changes
**None** - No migrations created yet. This milestone only set up project structure and connection strings. Database creation and schema migrations are Milestone 2.

## How it was Verified

### Build Verification
```bash
# Individual project builds
dotnet build src/CampusLibrary.Data
# Output: Build succeeded. 0 Warning(s). 0 Error(s).

dotnet build src/CampusLibrary.WebApi
# Output: Build succeeded. 0 Warning(s). 0 Error(s).

dotnet build src/CampusLibrary.WebMvc
# Output: Build succeeded. 0 Warning(s). 0 Error(s).

# Solution build
dotnet build
# Output: Build succeeded. All 3 projects compiled. 0 Warning(s). 0 Error(s).
```

### Project References Verification
```bash
dotnet sln list
# Output:
# Project(s)
# ----------
# src\CampusLibrary.Data\CampusLibrary.Data.csproj
# src\CampusLibrary.WebApi\CampusLibrary.WebApi.csproj
# src\CampusLibrary.WebMvc\CampusLibrary.WebMvc.csproj
```

### Configuration Verification
- ✅ Connection strings present in both `appsettings.Development.json` files
- ✅ CORS policy configured in WebApi for `http://10.0.2.2`
- ✅ Swagger enabled in WebApi Development environment
- ✅ Controllers registered in WebApi (not minimal APIs)
- ✅ All project references resolved without errors

## Known Limits / TODO

### Immediate Next Steps (Milestone 2)
1. Create entity classes: `Book.cs`, `Member.cs`, `Loan.cs` in `CampusLibrary.Data/Entities/`
2. Create `LibraryDbContext.cs` with DbSet properties
3. Add seed data with `HasData()`
4. Run first migration: `dotnet ef migrations add InitialCreate`
5. Apply migration: `dotnet ef database update`

### Deferred Items
- **Android project creation** - Requires Android Studio or Android SDK in PATH
  - Package: `com.example.campuslibraryapp`
  - Min SDK: 26, Target SDK: 34
  - Dependencies: Retrofit, Gson, Jetpack Compose, Material3
  - Manifest: INTERNET permission, `usesCleartextTraffic="true"`

### Current Limitations
- No entities or DbContext yet (empty Data project)
- No API controllers or DTOs
- No MVC controllers or views (only template files)
- Database does not exist yet (no migrations run)
- LocalDB availability not verified (will be checked in Milestone 2)

### Technical Notes
- Solution uses `.slnx` format (Visual Studio XML) instead of classic `.sln` - this is the .NET 10 default
- EF Core version 8.0.33 chosen to match .NET 8 target framework
- Connection string uses `TrustServerCertificate=True` to avoid certificate validation issues with LocalDB
- Android emulator CORS origin `http://10.0.2.2` is the loopback address from Android emulator to host machine
