using System.ComponentModel.DataAnnotations;

namespace CampusLibrary.Data.Entities
{
    public class Book
    {
        public int Id { get; set; }

        [Required]
        [MaxLength(100)]
        public string Title { get; set; }

        [MaxLength(100)]
        public string Author { get; set; }

        [Required]
        [MaxLength(13)]
        public string ISBN { get; set; }

        public int PublishedYear { get; set; }

        public ICollection<Loan> Loans { get; set; } = new List<Loan>();
    }
}