using CampusLibrary.Data.Entities;
using Microsoft.AspNetCore.Mvc;
using CampusLibrary.WebMvc.Services;

namespace CampusLibrary.WebMvc.Controllers;

public class MembersController : Controller
{
    private readonly IMemberService _memberService;

    public MembersController(IMemberService memberService)
    {
        _memberService = memberService;
    }

    // GET: Members
    public async Task<IActionResult> Index(CancellationToken cancellationToken)
    {
        var members = await _memberService.GetAllAsync(cancellationToken);
        return View(members);
    }

    // GET: Members/Create
    public IActionResult Create()
    {
        return View();
    }

    // POST: Members/Create
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Create(Member member, CancellationToken cancellationToken)
    {
        if (ModelState.IsValid)
        {
            if (!await _memberService.CreateAsync(member, cancellationToken))
            {
                ModelState.AddModelError("Email", "A member with this email already exists");
                return View(member);
            }

            return RedirectToAction(nameof(Index));
        }
        return View(member);
    }
}
