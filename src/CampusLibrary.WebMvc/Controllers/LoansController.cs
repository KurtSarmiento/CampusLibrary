using CampusLibrary.Data.Entities;
using Microsoft.AspNetCore.Mvc;
using CampusLibrary.WebMvc.Services;

namespace CampusLibrary.WebMvc.Controllers;

public class LoansController : Controller
{
    private readonly ILoanService _loanService;

    public LoansController(ILoanService loanService)
    {
        _loanService = loanService;
    }

    // GET: Loans
    public async Task<IActionResult> Index(CancellationToken cancellationToken)
    {
        var loans = await _loanService.GetOpenAsync(cancellationToken);

        return View(loans);
    }

    // POST: Loans/Return/5
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Return(int id, CancellationToken cancellationToken)
    {
        var result = await _loanService.ReturnAsync(id, cancellationToken);
        if (result == LoanReturnResult.NotFound)
        {
            return NotFound();
        }

        return RedirectToAction(nameof(Index));
    }
}
