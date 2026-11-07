using Microsoft.EntityFrameworkCore;

namespace CampusLibrary.Data
{
    public class LibraryDbContext : DbContext
    {
        public LibraryDbContext(DbContextOptions<LibraryDbContext> options) : base(options)
        {
        }

        public DbSet<Entities.Book> Books { get; set; }
        public DbSet<Entities.Member> Members { get; set; }
        public DbSet<Entities.Loan> Loans { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Configure ISBN as unique index
            modelBuilder.Entity<Entities.Book>()
                .HasIndex(b => b.ISBN)
                .IsUnique();

            // Enforce "at most one open loan per book" at the DB level (borrow race backstop).
            modelBuilder.Entity<Entities.Loan>()
                .HasIndex(l => l.BookId)
                .IsUnique()
                .HasFilter("[ReturnedAt] IS NULL");
        }
    }
}