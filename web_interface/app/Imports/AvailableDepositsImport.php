<?php

namespace App\Imports;

use App\Models\available_deposits;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;
use Carbon\Carbon;


class AvailableDepositsImport implements ToModel, WithHeadingRow
{
    /**
    * @param array $row
    *
    * @return \Illuminate\Database\Eloquent\Model|null
    */
    public function model(array $row)
    {
        return new available_deposits([
            'member_number' => $row['member_number'],
            'amount_deposited' => $row['amount_deposited'],
            'deposit_date' => $row['deposit_date'],
            'receipt_number' => $row['receipt_number'],
        ]);
    }
}
