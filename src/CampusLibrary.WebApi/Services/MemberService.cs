using CampusLibrary.Data.Entities;
using CampusLibrary.WebApi.DTOs;
using CampusLibrary.WebApi.Repositories;

namespace CampusLibrary.WebApi.Services;

public sealed class MemberService : IMemberService
{
    private readonly IMemberRepository _repository;

    public MemberService(IMemberRepository repository)
    {
        _repository = repository;
    }

    public async Task<IReadOnlyList<MemberDto>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        var members = await _repository.GetAllAsync(cancellationToken);
        return members.Select(DtoMapper.ToDto).ToList();
    }

    public async Task<MemberDto?> GetByIdAsync(int id, CancellationToken cancellationToken = default)
    {
        var member = await _repository.GetByIdAsync(id, cancellationToken);
        return member is null ? null : DtoMapper.ToDto(member);
    }

    public async Task<MemberCreateResult> CreateAsync(MemberDto dto, CancellationToken cancellationToken = default)
    {
        if (await _repository.ExistsWithEmailAsync(dto.Email, cancellationToken))
        {
            return MemberCreateResult.DuplicateEmail;
        }

        var member = new Member
        {
            Name = dto.Name,
            Email = dto.Email,
            JoinedDate = dto.JoinedDate == default ? DateTime.UtcNow : dto.JoinedDate
        };

        await _repository.AddAsync(member, cancellationToken);
        await _repository.SaveChangesAsync(cancellationToken);
        dto.Id = member.Id;
        dto.JoinedDate = member.JoinedDate;
        return MemberCreateResult.Success;
    }
}
