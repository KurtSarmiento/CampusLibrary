using System.ComponentModel.DataAnnotations;

namespace CampusLibrary.Data.Entities
{
    public class Loan
    {
        public int Id { get; set; }

        [Required]
        public int BookId { get; set; }
        public Book Book { get; set; }

        [Required]
        public int MemberId { get; set; }
        public Member Member { get; set; }

        [Required]
        public DateTime BorrowedAt { get; set; } = DateTime.UtcNow;

        public DateTime? ReturnedAt { get; set; }
    }
}