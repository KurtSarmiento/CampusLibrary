using System.ComponentModel.DataAnnotations;

namespace CampusLibrary.WebApi.DTOs;

public class BorrowRequestDto
{
    [Required]
    public int BookId { get; set; }

    [Required]
    public int MemberId { get; set; }
}
