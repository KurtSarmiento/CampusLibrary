using CampusLibrary.Data;
using CampusLibrary.Data.Entities;
using CampusLibrary.WebApi.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace CampusLibrary.WebApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class MembersController : ControllerBase
{
    private readonly LibraryDbContext _context;

    public MembersController(LibraryDbContext context)
    {
        _context = context;
    }

    // GET: api/members
    [HttpGet]
    public async Task<ActionResult<IEnumerable<MemberDto>>> GetMembers()
    {
        var members = await _context.Members
            .Select(m => new MemberDto
            {
                Id = m.Id,
                Name = m.Name,
                Email = m.Email,
                JoinedDate = m.JoinedDate
            })
            .ToListAsync();

        return Ok(members);
    }

    // GET: api/members/5
    [HttpGet("{id}")]
    public async Task<ActionResult<MemberDto>> GetMember(int id)
    {
        var member = await _context.Members
            .Where(m => m.Id == id)
            .Select(m => new MemberDto
            {
                Id = m.Id,
                Name = m.Name,
                Email = m.Email,
                JoinedDate = m.JoinedDate
            })
            .FirstOrDefaultAsync();

        if (member == null)
        {
            return NotFound(new { Message = $"Member with ID {id} not found" });
        }

        return Ok(member);
    }

    // POST: api/members
    [HttpPost]
    public async Task<ActionResult<MemberDto>> CreateMember(MemberDto dto)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }

        // Check for duplicate email
        var existingMember = await _context.Members
            .FirstOrDefaultAsync(m => m.Email == dto.Email);

        if (existingMember != null)
        {
            return Conflict(new { Message = $"A member with email {dto.Email} already exists" });
        }

        var member = new Member
        {
            Name = dto.Name,
            Email = dto.Email,
            JoinedDate = dto.JoinedDate == default ? DateTime.UtcNow : dto.JoinedDate
        };

        _context.Members.Add(member);
        await _context.SaveChangesAsync();

        dto.Id = member.Id;
        return CreatedAtAction(nameof(GetMember), new { id = member.Id }, dto);
    }
}
