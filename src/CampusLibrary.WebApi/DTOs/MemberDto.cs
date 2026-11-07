using System.ComponentModel.DataAnnotations;

namespace CampusLibrary.WebApi.DTOs;

public class MemberDto
{
    public int Id { get; set; }

    [Required]
    [MaxLength(100)]
    public string Name { get; set; } = string.Empty;

    [Required]
    [MaxLength(100)]
    public string Email { get; set; } = string.Empty;

    public DateTime JoinedDate { get; set; }
}
