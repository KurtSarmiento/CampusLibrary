using CampusLibrary.WebApi.DTOs;
using CampusLibrary.WebApi.Services;
using Microsoft.AspNetCore.Mvc;

namespace CampusLibrary.WebApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class LoansController : ControllerBase
{
    private readonly ILoanService _loanService;

    public LoansController(ILoanService loanService)
    {
        _loanService = loanService;
    }

    [HttpPost]
    public async Task<ActionResult<LoanDto>> BorrowBook(BorrowRequestDto request, CancellationToken cancellationToken)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }

        var result = await _loanService.BorrowAsync(request, cancellationToken);
        return result.Status switch
        {
            BorrowResultStatus.BookNotFound => NotFound(new { Message = $"Book with ID {request.BookId} not found" }),
            BorrowResultStatus.MemberNotFound => NotFound(new { Message = $"Member with ID {request.MemberId} not found" }),
            BorrowResultStatus.BookAlreadyOnLoan => Conflict(new { Message = $"Book with ID {request.BookId} is already on loan" }),
            BorrowResultStatus.Conflict => Conflict(new { Message = $"Book with ID {request.BookId} is already on loan" }),
            _ => CreatedAtAction(nameof(GetLoan), new { id = result.Loan!.Id }, result.Loan)
        };
    }

    [HttpPost("{id}/return")]
    public async Task<IActionResult> ReturnBook(int id, CancellationToken cancellationToken)
    {
        var result = await _loanService.ReturnAsync(id, cancellationToken);
        return result switch
        {
            LoanReturnResult.NotFound => NotFound(new { Message = $"Loan with ID {id} not found" }),
            LoanReturnResult.AlreadyReturned => Conflict(new { Message = $"Loan with ID {id} has already been returned" }),
            _ => NoContent()
        };
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<LoanDto>>> GetLoans(CancellationToken cancellationToken)
    {
        return Ok(await _loanService.GetAllAsync(cancellationToken));
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<LoanDto>> GetLoan(int id, CancellationToken cancellationToken)
    {
        var loan = await _loanService.GetByIdAsync(id, cancellationToken);
        return loan is null
            ? NotFound(new { Message = $"Loan with ID {id} not found" })
            : Ok(loan);
    }
}