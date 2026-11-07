using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace CampusLibrary.Data.Migrations
{
    /// <inheritdoc />
    public partial class AddUniqueOpenLoanIndex : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateIndex(
                name: "IX_Loans_BookId_Open",
                table: "Loans",
                column: "BookId",
                unique: true,
                filter: "[ReturnedAt] IS NULL");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropIndex(
                name: "IX_Loans_BookId_Open",
                table: "Loans");
        }
    }
}
