# Capability Map: CampusLibrary

| Module id | Responsibility | Depends on |
|---|---|---|
| data | EF Core entities, DbContext, migrations | — |
| web-api | REST API endpoints, controllers | data |
| web-mvc | Admin site with Razor views | data, web-api |
| android | Android app calling the Web API | web-api |

Build order: data → web-api, web-mvc → android