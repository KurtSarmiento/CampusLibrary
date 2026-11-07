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
}
