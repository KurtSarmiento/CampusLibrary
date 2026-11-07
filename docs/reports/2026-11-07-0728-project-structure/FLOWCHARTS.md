# Flowcharts: Project Structure Setup

## 1. Solution Setup Flow

```mermaid
flowchart TD
    Start([Start: Milestone 1]) --> CreateSln[Create CampusLibrary.slnx]
    CreateSln --> CreateData[Create Data Class Library]
    CreateData --> AddEFPackages[Add EF Core Packages<br/>SqlServer & Design 8.0.33]
    AddEFPackages --> AddDataToSln[Add Data to Solution]
    AddDataToSln --> BuildData{Build Data<br/>Success?}
    BuildData -->|No| FixDataErrors[Fix Build Errors]
    FixDataErrors --> BuildData
    BuildData -->|Yes| CreateWebApi[Create WebApi Project]
    
    CreateWebApi --> AddApiPackages[Add EF Core to WebApi]
    AddApiPackages --> RefApiData[Add Project Reference<br/>WebApi → Data]
    RefApiData --> ConfigCORS[Configure CORS<br/>for Android Emulator]
    ConfigCORS --> ConfigConnStr1[Add Connection String<br/>to appsettings.Development.json]
    ConfigConnStr1 --> BuildApi{Build WebApi<br/>Success?}
    BuildApi -->|No| FixApiErrors[Fix Build Errors]
    FixApiErrors --> BuildApi
    BuildApi -->|Yes| AddApiToSln[Add WebApi to Solution]
    
    AddApiToSln --> CreateWebMvc[Create WebMvc Project]
    CreateWebMvc --> AddMvcPackages[Add EF Core to WebMvc]
    AddMvcPackages --> RefMvcData[Add Project Reference<br/>WebMvc → Data]
    RefMvcData --> ConfigConnStr2[Add Connection String<br/>to appsettings.Development.json]
    ConfigConnStr2 --> BuildMvc{Build WebMvc<br/>Success?}
    BuildMvc -->|No| FixMvcErrors[Fix Build Errors]
    FixMvcErrors --> BuildMvc
    BuildMvc -->|Yes| AddMvcToSln[Add WebMvc to Solution]
    
    AddMvcToSln --> BuildSolution{Build All<br/>dotnet build<br/>Success?}
    BuildSolution -->|No| FixSlnErrors[Fix Solution Errors]
    FixSlnErrors --> BuildSolution
    BuildSolution -->|Yes| AndroidCheck{Android SDK<br/>Available?}
    
    AndroidCheck -->|Yes| CreateAndroid[Create Android Project]
    CreateAndroid --> ConfigManifest[Configure Manifest<br/>INTERNET permission<br/>cleartext traffic]
    ConfigManifest --> BuildAndroid{./gradlew<br/>assembleDebug<br/>Success?}
    BuildAndroid -->|No| FixAndroidErrors[Fix Android Errors]
    FixAndroidErrors --> BuildAndroid
    BuildAndroid -->|Yes| Complete([Phase 1 Complete:<br/>All 4 Projects Created])
    
    AndroidCheck -->|No| DeferAndroid[Defer Android<br/>Document in TODO]
    DeferAndroid --> PartialComplete([Phase 1 Partial:<br/>3/4 Projects Created])
    
    style CreateSln fill:#e1f5ff
    style CreateData fill:#e1f5ff
    style CreateWebApi fill:#e1f5ff
    style CreateWebMvc fill:#e1f5ff
    style CreateAndroid fill:#fff3e0
    style Complete fill:#c8e6c9
    style PartialComplete fill:#fff9c4
```

## 2. Build Verification Sequence

```mermaid
sequenceDiagram
    participant Dev as Developer
    participant CLI as dotnet CLI
    participant Data as CampusLibrary.Data
    participant WebApi as CampusLibrary.WebApi
    participant WebMvc as CampusLibrary.WebMvc
    participant Sln as Solution
    
    Dev->>CLI: dotnet build src/CampusLibrary.Data
    CLI->>Data: Restore NuGet packages
    Data-->>CLI: EF Core 8.0.33 restored
    CLI->>Data: Compile project
    Data-->>CLI: Build succeeded ✓
    CLI-->>Dev: CampusLibrary.Data.dll created
    
    Dev->>CLI: dotnet build src/CampusLibrary.WebApi
    CLI->>WebApi: Restore NuGet packages
    WebApi-->>CLI: Packages restored
    CLI->>Data: Build referenced project
    Data-->>CLI: Already built ✓
    CLI->>WebApi: Compile project
    WebApi-->>CLI: Build succeeded ✓
    CLI-->>Dev: CampusLibrary.WebApi.dll created
    
    Dev->>CLI: dotnet build src/CampusLibrary.WebMvc
    CLI->>WebMvc: Restore NuGet packages
    WebMvc-->>CLI: Packages restored
    CLI->>Data: Build referenced project
    Data-->>CLI: Already built ✓
    CLI->>WebMvc: Compile project
    WebMvc-->>CLI: Build succeeded ✓
    CLI-->>Dev: CampusLibrary.WebMvc.dll created
    
    Dev->>CLI: dotnet build (solution)
    CLI->>Sln: Parse CampusLibrary.slnx
    Sln-->>CLI: 3 projects found
    CLI->>Data: Build
    Data-->>CLI: Already built ✓
    CLI->>WebApi: Build
    WebApi-->>CLI: Already built ✓
    CLI->>WebMvc: Build
    WebMvc-->>CLI: Already built ✓
    CLI-->>Dev: Solution build succeeded<br/>0 Warning(s), 0 Error(s) ✓
```

## 3. Project Dependency Graph

```mermaid
graph LR
    subgraph Solution["CampusLibrary.slnx"]
        Data["CampusLibrary.Data<br/>(Class Library)<br/>📦 EF Core 8.0.33"]
        WebApi["CampusLibrary.WebApi<br/>(Web API)<br/>🌐 REST + Swagger<br/>🔗 CORS for Android"]
        WebMvc["CampusLibrary.WebMvc<br/>(MVC)<br/>🖥️ Razor + Bootstrap"]
        Android["CampusLibraryApp<br/>(Android Kotlin)<br/>📱 Jetpack Compose<br/>⚠️ NOT CREATED YET"]
    end
    
    WebApi -.->|Project Reference| Data
    WebMvc -.->|Project Reference| Data
    Android -.->|HTTP Calls<br/>Runtime Only| WebApi
    
    style Data fill:#bbdefb
    style WebApi fill:#c5e1a5
    style WebMvc fill:#fff59d
    style Android fill:#ffccbc,stroke-dasharray: 5 5
```

## 4. Connection String Configuration Flow

```mermaid
flowchart TD
    Start([Configuration Needed]) --> WhichProject{Which Project?}
    
    WhichProject -->|WebApi| OpenApiSettings[Open src/CampusLibrary.WebApi/<br/>appsettings.Development.json]
    WhichProject -->|WebMvc| OpenMvcSettings[Open src/CampusLibrary.WebMvc/<br/>appsettings.Development.json]
    
    OpenApiSettings --> AddApiConnStr[Add ConnectionStrings section:<br/>DefaultConnection]
    OpenMvcSettings --> AddMvcConnStr[Add ConnectionStrings section:<br/>DefaultConnection]
    
    AddApiConnStr --> ConnStrValue[Set Value:<br/>Server=localdb\\MSSQLLocalDB<br/>Database=CampusLibraryDb<br/>Trusted_Connection=True<br/>TrustServerCertificate=True]
    AddMvcConnStr --> ConnStrValue
    
    ConnStrValue --> SaveFile[Save File]
    SaveFile --> TestBuild{Build<br/>Success?}
    
    TestBuild -->|Yes| Ready([Ready for<br/>DbContext Registration<br/>in Milestone 2])
    TestBuild -->|No| CheckError{Error Type?}
    
    CheckError -->|JSON Syntax| FixJSON[Fix JSON Format:<br/>Check commas, quotes, brackets]
    CheckError -->|Path Error| FixPath[Verify File Path:<br/>Must be in project root]
    CheckError -->|Other| Investigate[Investigate Build Error]
    
    FixJSON --> TestBuild
    FixPath --> TestBuild
    Investigate --> TestBuild
    
    style ConnStrValue fill:#fff9c4
    style Ready fill:#c8e6c9
```

## 5. CORS Configuration for Android

```mermaid
flowchart TD
    Start([Android App Needs<br/>to Call API]) --> EmulatorIP[Android Emulator IP:<br/>http://10.0.2.2]
    
    EmulatorIP --> OpenProgram[Open WebApi Program.cs]
    OpenProgram --> AddCORSService[Add CORS Service:<br/>builder.Services.AddCors]
    AddCORSService --> DefinePolicy[Define Policy:<br/>AndroidEmulator]
    DefinePolicy --> SetOrigin[WithOrigins:<br/>http://10.0.2.2]
    SetOrigin --> AllowAll[AllowAnyHeader<br/>AllowAnyMethod]
    AllowAll --> UseCORS[Add Middleware:<br/>app.UseCors<br/>AndroidEmulator]
    
    UseCORS --> TestAPI{API<br/>Starts?}
    TestAPI -->|No| FixCORS[Check Syntax<br/>Rebuild Project]
    FixCORS --> TestAPI
    
    TestAPI -->|Yes| AndroidCall[Android App Makes Request:<br/>GET http://10.0.2.2:5000/api/books]
    AndroidCall --> CORSCheck{CORS<br/>Allowed?}
    
    CORSCheck -->|No: 403 Forbidden| CheckOrigin[Verify Origin Matches:<br/>http://10.0.2.2<br/>not localhost or 127.0.0.1]
    CheckOrigin --> RestartAPI[Restart API<br/>to Apply Changes]
    RestartAPI --> AndroidCall
    
    CORSCheck -->|Yes: 200 OK| Success([✓ Android Can Access API])
    
    style EmulatorIP fill:#fff3e0
    style SetOrigin fill:#fff9c4
    style Success fill:#c8e6c9
```

## 6. Entity Relationship Diagram (Milestone 2 Preview)

```mermaid
erDiagram
    BOOKS ||--o{ LOANS : "has"
    MEMBERS ||--o{ LOANS : "borrows"
    
    BOOKS {
        int Id PK
        string ISBN UK "Max 13, Required"
        string Title "Max 200, Required"
        string Author "Max 100, Nullable"
        int PublishedYear
    }
    
    MEMBERS {
        int Id PK
        string Name "Max 100, Required"
        string Email UK "Max 100, Required"
        datetime JoinedDate
    }
    
    LOANS {
        int Id PK
        int BookId FK
        int MemberId FK
        datetime BorrowedAt "Required"
        datetime ReturnedAt "Nullable - NULL means open loan"
    }
```

**Note:** This ERD shows the planned schema for Milestone 2. In Phase 1 (current milestone), no entities or database tables exist yet - only the project structure and connection strings are configured.
