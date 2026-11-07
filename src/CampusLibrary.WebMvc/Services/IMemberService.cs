using CampusLibrary.Data.Entities;

namespace CampusLibrary.WebMvc.Services;

public interface IMemberService
{
    Task<IReadOnlyList<Member>> GetAllAsync(CancellationToken cancellationToken = default);
    Task<bool> CreateAsync(Member member, CancellationToken cancellationToken = default);
}
