using System.ComponentModel.DataAnnotations;

namespace CampusLibrary.Data.Entities
{
    public class Member
    {
        public int Id { get; set; }

        [Required]
        [MaxLength(100)]
        public string Name { get; set; }

        [Required]
        [EmailAddress]
        [MaxLength(100)]
        public string Email { get; set; }

        public DateTime JoinedDate { get; set; }

        public ICollection<Loan> Loans { get; set; } = new List<Loan>();
    }
}