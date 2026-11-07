namespace CampusLibrary.WebApi.DTOs;

public class LoanDto
{
    public int Id { get; set; }
    public int BookId { get; set; }
    public int MemberId { get; set; }
    public DateTime BorrowedAt { get; set; }
    public DateTime? ReturnedAt { get; set; }

    // Navigation properties for related data
    public BookDto Book { get; set; } = null!;
    public MemberDto Member { get; set; } = null!;
}
