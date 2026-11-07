using CampusLibrary.Data.Entities;
using CampusLibrary.WebMvc.Repositories;

namespace CampusLibrary.WebMvc.Services;

public sealed class MemberService : IMemberService
{
    private readonly IMemberRepository _repository;

    public MemberService(IMemberRepository repository)
    {
        _repository = repository;
    }

    public Task<IReadOnlyList<Member>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        return _repository.GetAllAsync(cancellationToken);
    }

    public async Task<bool> CreateAsync(Member member, CancellationToken cancellationToken = default)
    {
        if (await _repository.ExistsWithEmailAsync(member.Email, cancellationToken))
        {
            return false;
        }

        if (member.JoinedDate == default)
        {
            member.JoinedDate = DateTime.UtcNow;
        }

        await _repository.AddAsync(member, cancellationToken);
        await _repository.SaveChangesAsync(cancellationToken);
        return true;
    }
}
