using System.ComponentModel.DataAnnotations;

namespace CampusLibrary.WebApi.DTOs;

public class BookDto
{
    public int Id { get; set; }

    [Required]
    [MaxLength(13)]
    public string ISBN { get; set; } = string.Empty;

    [Required]
    [MaxLength(200)]
    public string Title { get; set; } = string.Empty;

    [MaxLength(100)]
    public string? Author { get; set; }

    public int PublishedYear { get; set; }

    /// <summary>True when the book has an open (not-yet-returned) loan.</summary>
    public bool IsOnLoan { get; set; }

    /// <summary>Name of the member who currently has the book on loan, if any.</summary>
    public string? LoanedToMemberName { get; set; }
}
