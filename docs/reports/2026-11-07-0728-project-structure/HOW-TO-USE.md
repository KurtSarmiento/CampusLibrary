# How to Use: CampusLibrary Solution (Phase 1 Complete)

## Prerequisites

### Required (Verified Working)
- ✅ .NET SDK 8.0 or higher (you have 10.0.403)
- ✅ Visual Studio Code, Visual Studio 2022, or any C# IDE
- ✅ Git (for version control)

### Required for Next Steps (Milestone 2)
- SQL Server 2022 Express or LocalDB (`(localdb)\MSSQLLocalDB`)
- Entity Framework Core CLI tools (check with `dotnet ef --version`)

### Required Later (Milestones 6-7)
- Android Studio (for Android project)
- Android SDK API 26+ (Android 8.0 or higher)

## Step-by-Step: Working with the Solution

### 1. Open the Solution

**Option A: Visual Studio 2022**
```bash
# Open the .slnx file
start CampusLibrary.slnx
```

**Option B: Visual Studio Code**
```bash
# Open the workspace folder
code .
```

**Option C: JetBrains Rider**
```bash
# Open the solution
rider CampusLibrary.slnx
```

### 2. Build All Projects

```bash
# From the solution root directory
dotnet build

# Expected Output:
# Build succeeded.
#     0 Warning(s)
#     0 Error(s)
```

### 3. Restore NuGet Packages (if needed)

```bash
dotnet restore
```

### 4. Run the Web API

```bash
# Start the API (Development mode, Swagger enabled)
dotnet run --project src/CampusLibrary.WebApi

# The API will start on https://localhost:5xxx and http://localhost:5xxx
# Swagger UI available at: https://localhost:5xxx/swagger
```

**Expected console output:**
```
info: Microsoft.Hosting.Lifetime[14]
      Now listening on: http://localhost:5000
info: Microsoft.Hosting.Lifetime[14]
      Now listening on: https://localhost:5001
```

Open your browser to `https://localhost:5001/swagger` to see the Swagger UI.

### 5. Run the Web MVC App

```bash
# Start the MVC app (separate terminal)
dotnet run --project src/CampusLibrary.WebMvc

# The app will start on https://localhost:5xxx
```

Open your browser to `https://localhost:5xxx` to see the default MVC home page.

### 6. Development Workflow

**Watch Mode (auto-reload on file changes):**
```bash
# For API
dotnet watch --project src/CampusLibrary.WebApi

# For MVC
dotnet watch --project src/CampusLibrary.WebMvc
```

**Build individual projects:**
```bash
dotnet build src/CampusLibrary.Data
dotnet build src/CampusLibrary.WebApi
dotnet build src/CampusLibrary.WebMvc
```

## Example: What You Can Do Now

### Check the Solution Structure

```bash
# List all projects in solution
dotnet sln list

# Expected Output:
# Project(s)
# ----------
# src\CampusLibrary.Data\CampusLibrary.Data.csproj
# src\CampusLibrary.WebApi\CampusLibrary.WebApi.csproj
# src\CampusLibrary.WebMvc\CampusLibrary.WebMvc.csproj
```

### View Connection Strings

**WebApi:**
```bash
cat src/CampusLibrary.WebApi/appsettings.Development.json
```

**WebMvc:**
```bash
cat src/CampusLibrary.WebMvc/appsettings.Development.json
```

Both should show:
```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Server=(localdb)\\MSSQLLocalDB;Database=CampusLibraryDb;Trusted_Connection=True;TrustServerCertificate=True;"
  }
}
```

### Verify CORS Configuration (for Android)

The WebApi is configured to accept requests from Android emulator (`http://10.0.2.2`). Once you have API endpoints and an Android app, the emulator will be able to call:
- `http://10.0.2.2:5000/api/books`
- `http://10.0.2.2:5000/api/members`
- `http://10.0.2.2:5000/api/loans`

## What's NOT Available Yet

❌ **No database tables** - Entities and migrations are Milestone 2  
❌ **No API endpoints** - Controllers are Milestones 3-4  
❌ **No MVC pages** - Views are Milestone 5  
❌ **No Android app** - Android project is Milestones 6-7  

The projects currently have only template/scaffolding code.

## Troubleshooting

### Issue: "Project file does not exist"

**Symptom:**
```bash
dotnet build CampusLibrary.sln
# Error: Project file does not exist
```

**Solution:**
The solution uses `.slnx` format (XML). Use `dotnet build` without specifying the file:
```bash
dotnet build
```

### Issue: "Package restore failed"

**Symptom:**
```
error NU1202: Package is not compatible with net8.0
```

**Solution:**
Ensure you're using .NET SDK 8.0 or higher:
```bash
dotnet --version
# Should show 8.0.x or higher
```

### Issue: "EF Core tools not found"

**Symptom (when attempting migrations in Milestone 2):**
```bash
dotnet ef
# Command 'dotnet ef' not found
```

**Solution:**
Install EF Core CLI tools globally:
```bash
dotnet tool install --global dotnet-ef
```

### Issue: "LocalDB not found"

**Symptom (will occur in Milestone 2):**
```
A network-related or instance-specific error occurred while establishing a connection to SQL Server
```

**Solution:**
Install SQL Server Express or LocalDB from:
https://www.microsoft.com/en-us/sql-server/sql-server-downloads

Verify LocalDB is running:
```bash
sqllocaldb info
# Should list: MSSQLLocalDB
```

### Issue: Port already in use

**Symptom:**
```
Unable to bind to https://localhost:5001
```

**Solution:**
Change the port in `launchSettings.json` or stop the conflicting application.

## Next Steps (Milestone 2)

Once you're ready to continue:

1. **Create entities** in `src/CampusLibrary.Data/Entities/`
   - `Book.cs`
   - `Member.cs`
   - `Loan.cs`

2. **Create DbContext** in `src/CampusLibrary.Data/`
   - `LibraryDbContext.cs`

3. **Add seed data** and run first migration

See CLAUDE.md section 6 for the full milestone sequence.
