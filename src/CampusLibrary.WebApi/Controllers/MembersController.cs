using CampusLibrary.WebApi.DTOs;
using CampusLibrary.WebApi.Services;
using Microsoft.AspNetCore.Mvc;

namespace CampusLibrary.WebApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class MembersController : ControllerBase
{
    private readonly IMemberService _memberService;

    public MembersController(IMemberService memberService)
    {
        _memberService = memberService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<MemberDto>>> GetMembers(CancellationToken cancellationToken)
    {
        return Ok(await _memberService.GetAllAsync(cancellationToken));
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<MemberDto>> GetMember(int id, CancellationToken cancellationToken)
    {
        var member = await _memberService.GetByIdAsync(id, cancellationToken);
        return member is null
            ? NotFound(new { Message = $"Member with ID {id} not found" })
            : Ok(member);
    }

    [HttpPost]
    public async Task<ActionResult<MemberDto>> CreateMember(MemberDto dto, CancellationToken cancellationToken)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }

        var result = await _memberService.CreateAsync(dto, cancellationToken);
        if (result == MemberCreateResult.DuplicateEmail)
        {
            return Conflict(new { Message = $"A member with email {dto.Email} already exists" });
        }

        return CreatedAtAction(nameof(GetMember), new { id = dto.Id }, dto);
    }
}