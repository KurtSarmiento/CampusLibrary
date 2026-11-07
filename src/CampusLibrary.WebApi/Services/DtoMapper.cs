using CampusLibrary.Data.Entities;
using CampusLibrary.WebApi.DTOs;

namespace CampusLibrary.WebApi.Services;

internal static class DtoMapper
{
    public static BookDto ToDto(Book book)
    {
        var openLoan = book.Loans.FirstOrDefault(loan => loan.ReturnedAt == null);

        return new BookDto
        {
            Id = book.Id,
            ISBN = book.ISBN,
            Title = book.Title,
            Author = book.Author,
            PublishedYear = book.PublishedYear,
            IsOnLoan = openLoan is not null,
            LoanedToMemberName = openLoan?.Member?.Name
        };
    }

    public static MemberDto ToDto(Member member)
    {
        return new MemberDto
        {
            Id = member.Id,
            Name = member.Name,
            Email = member.Email,
            JoinedDate = member.JoinedDate
        };
    }

    public static LoanDto ToDto(Loan loan)
    {
        return new LoanDto
        {
            Id = loan.Id,
            BookId = loan.BookId,
            MemberId = loan.MemberId,
            BorrowedAt = loan.BorrowedAt,
            ReturnedAt = loan.ReturnedAt,
            Book = ToDtoWithoutLoanStatus(loan.Book),
            Member = ToDto(loan.Member)
        };
    }

    private static BookDto ToDtoWithoutLoanStatus(Book book)
    {
        return new BookDto
        {
            Id = book.Id,
            ISBN = book.ISBN,
            Title = book.Title,
            Author = book.Author,
            PublishedYear = book.PublishedYear
        };
    }
}
