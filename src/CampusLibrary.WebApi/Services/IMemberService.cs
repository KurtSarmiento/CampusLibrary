using CampusLibrary.WebApi.DTOs;

namespace CampusLibrary.WebApi.Services;

public interface IMemberService
{
    Task<IReadOnlyList<MemberDto>> GetAllAsync(CancellationToken cancellationToken = default);
    Task<MemberDto?> GetByIdAsync(int id, CancellationToken cancellationToken = default);
    Task<MemberCreateResult> CreateAsync(MemberDto dto, CancellationToken cancellationToken = default);
}

public enum MemberCreateResult
{
    Success,
    DuplicateEmail
}
